package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.transfer.base.TranslationTableWriter
import com.andro.spreadi18ncore.transfer.translation.TranslationTable
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.nio.file.Path

internal class ExcelTranslationTableWriter(private val destinationFilePath: Path) : TranslationTableWriter {

    override fun write(translationTable: TranslationTable) {
        XSSFWorkbook().use { workbook ->
            workbook.createSheet().copyFrom(translationTable)
            val outputStream = FileOutputStream(destinationFilePath.toFile())
            workbook.write(outputStream)
        }
    }

    private fun XSSFSheet.copyFrom(translationTable: TranslationTable) {
        //fill header row
        val headerRow = createRow(0)
        headerRow.createCell(0).setCellValue("key")
        translationTable.languageTags.withIndex().forEach {
            headerRow.createCell(it.index+1).setCellValue(it.value.canonical)
        }
        //fill translations
        translationTable.translationRows.withIndex().forEach { indexedTranslations ->
            val translationRow = createRow(indexedTranslations.index+1)
            with(indexedTranslations.value) {
                translationRow.createCell(0).setCellValue(key)
                values.withIndex().forEach { indexedValue ->
                    translationRow.createCell(indexedValue.index+1).setCellValue(indexedValue.value)
                }
            }
        }
    }
}