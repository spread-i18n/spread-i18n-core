package com.andro.spreadi18ncore.sourcesheet

import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.sourcetargetmatching.Locales.Companion.allLocales
import com.andro.spreadi18ncore.sourcetargetmatching.SourceColumn
import com.andro.spreadi18ncore.targetproject.ProjectType
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

internal class HeaderRowNotFound(): ImportException("Header row not found in the source file.")

internal data class HeaderRow(val rowInDocument: Int, val sourceColumns: Set<SourceColumn>,
                              private val translationKeyColumns: TranslationKeyColumns
) {

    fun indexOfTranslationKeyColumnForProjectType(projectType: ProjectType): Int {
        if (translationKeyColumns.containsTranslationKeyColumnFor(projectType.translationKeyType)) {
            return translationKeyColumns.getTranslationKeyColumn(projectType.translationKeyType).columnIndex
        }
        return translationKeyColumns.getTranslationKeyColumn(TranslationKeyType.General).columnIndex
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

internal typealias LocaleCell = SourceColumn
internal typealias KeyCell = TranslationKeyColumn

internal object RowAnalyser {

    fun toHeaderRow(indexedRow: IndexedValue<Row>): HeaderRow? {
        val keyCells = TranslationKeyColumns()
        val localeCells = mutableSetOf<SourceColumn>()

        fun saveLocaleCell(localeCellCandidate: IndexedValue<Cell>): Boolean {
            return toLocaleCell(localeCellCandidate)?.let {
                localeCells.add(it)
                true
            } ?: false
        }
        
        fun saveKeyCell(localeCellCandidate: IndexedValue<Cell>): Boolean {
            return toKeyCell(localeCellCandidate)?.let {
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

    private fun toLocaleCell(localeCellCandidate: IndexedValue<Cell>): LocaleCell? {
        val localeCandidate = localeCellCandidate.value.stringCellValue.trim()
        if (localeCandidate.isEmpty()) {
            return null
        }
        allLocales.findLocale(localeCandidate)?.let {
            return LocaleCell(localeCandidate, localeCellCandidate.index)
        }
        return null
    }

    private fun toKeyCell(keyCellCandidate: IndexedValue<Cell>): KeyCell? {
        val tokens = keyCellCandidate.value.stringCellValue.trim()
                .split(" ").filter { it.isNotBlank() }.map { it.toLowerCase() }
        if (tokens.isEmpty()) {
            return null
        }
        for (translationKeyType in TranslationKeyType.values()) {
            translationKeyType.cellText.find { tokens.contains(it) }?.let {
                return KeyCell(keyCellCandidate.index, translationKeyType)
            }
        }
        return null
    }
}
