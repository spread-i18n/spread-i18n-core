package com.andro.spreadi18ncore.transfer.importing

import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.excel.*
import com.andro.spreadi18ncore.excel.HeaderRow
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.transfer.translation.TranslationTable
import com.andro.spreadi18ncore.transfer.base.TranslationTableReader
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.transform
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import java.nio.file.Path


fun Row.getCell(columnIndex: ColumnIndex): Cell? = getCell(columnIndex.value)
internal class ExcelTranslationTableReader(
    private val sourceFilePath: Path,
    private val projectType: ProjectType,
    valueTransformations: ValueTransformations?
) :
    TranslationTableReader {

    override fun read(): TranslationTable {
        return workbook(sourceFilePath).use { workbook ->
            workbook.firstSheet.translationTable
        }
    }

    private val valueTransformation: ValueTransformation? by lazy {
        if (valueTransformations != null) {
            CustomValueTransformation(valueTransformations)
        } else {
            null
        }
    }

    private val XSSFSheet.translationTable: TranslationTable
        get() {
            val headerRow = HeaderRow.getFrom(this)
            val keyColumnIndex = headerRow.columnIndexForProjectType(projectType)
            val table = TranslationTable(headerRow.languageTags)
            rows.skipTo(headerRow.rowWithFirstTranslation).forEach { row ->
                row.getNotBlank(keyColumnIndex)?.let { key ->
                    headerRow.localeCells.forEach { localeCell ->
                        row.getNotNull(localeCell.columnIndex)?.let { value ->
                            table.setValue(localeCell.languageTag, KeyValue(key, value.transform(valueTransformation)))
                        }
                    }
                }
            }
            return table
        }

    private val HeaderRow.languageTags get() = localeCells.map { it.languageTag }
    private fun Row.getNotBlank(columnIndex: ColumnIndex): String? =
        getCell(columnIndex)?.stringCellValue?.let { it.ifBlank { null } }
    private fun Row.getNotNull(columnIndex: ColumnIndex): String? =
        getCell(columnIndex)?.stringCellValue
}