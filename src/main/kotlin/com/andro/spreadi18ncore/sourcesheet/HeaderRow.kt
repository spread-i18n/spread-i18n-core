package com.andro.spreadi18ncore.sourcesheet

import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.importing.Locales.Companion.allLocales
import com.andro.spreadi18ncore.targetproject.ProjectType
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

internal class HeaderRowNotFound(): ImportException("Header row not found in the source file.")

internal data class HeaderRow(val rowInDocument: Int, val localeCells: LocaleCells,
                              private val keyCells: KeyCells
) {

    fun columnIndexForProjectType(projectType: ProjectType): ColumnIndex {
        if (keyCells.containsKeyCellFor(projectType.translationKeyType)) {
            return keyCells.getKeyCell(projectType.translationKeyType).columnIndex
        }
        return keyCells.getKeyCell(TranslationKeyType.General).columnIndex
    }

    val rowWithFirstTranslation = rowInDocument+1

    companion object {

        fun getFrom(sheet: Sheet): HeaderRow {
            return findIn(sheet) ?: throw HeaderRowNotFound()
        }

        fun findIn(sheet: Sheet): HeaderRow? {
            sheet.rowIterator().withIndex().forEach { indexedRow ->
                RowAnalyser.toHeaderRow(indexedRow)?.let { return it }
            }
            return null
        }
    }
}

internal object RowAnalyser {

    fun toHeaderRow(indexedRow: IndexedValue<Row>): HeaderRow? {
        val keyCells = KeyCells()
        val localeCells = LocaleCells()

        fun saveLocaleCell(localeCellCandidate: IndexedValue<Cell>): Boolean {
            return toLocaleCell(localeCellCandidate, indexedRow.index)?.let {
                localeCells.add(it)
                true
            } ?: false
        }
        
        fun saveKeyCell(localeCellCandidate: IndexedValue<Cell>): Boolean {
            return toKeyCell(localeCellCandidate, indexedRow.index)?.let {
                keyCells.add(it)
                true
            } ?: false
        }
        
        indexedRow.value.cellIterator().withIndex().forEach { indexedCell ->
            saveLocaleCell(indexedCell) or saveKeyCell(indexedCell)
        }

        val isHeaderRow = keyCells.isNotEmpty() and localeCells.isNotEmpty()
        if (isHeaderRow) {
            return HeaderRow(indexedRow.index, localeCells, keyCells)
        }
        return null
    }

    private fun toLocaleCell(localeCellCandidate: IndexedValue<Cell>, rowIndex: Int): LocaleCell? {
        val localeCandidate = localeCellCandidate.value.stringCellValue.trim()
        if (localeCandidate.isEmpty()) {
            return null
        }
        allLocales.findLocale(localeCandidate)?.let {
            return LocaleCell(RowIndex(rowIndex), ColumnIndex(localeCellCandidate.index), localeCandidate)
        }
        return null
    }

    private fun toKeyCell(keyCellCandidate: IndexedValue<Cell>, rowIndex: Int): KeyCell? {
        val tokens = keyCellCandidate.value.stringCellValue.trim()
                .split(" ").filter { it.isNotBlank() }.map { it.toLowerCase() }
        if (tokens.isEmpty()) {
            return null
        }
        for (translationKeyType in TranslationKeyType.values()) {
            translationKeyType.cellText.find { tokens.contains(it) }?.let {
                return KeyCell(RowIndex(rowIndex), ColumnIndex(keyCellCandidate.index), translationKeyType)
            }
        }
        return null
    }
}
