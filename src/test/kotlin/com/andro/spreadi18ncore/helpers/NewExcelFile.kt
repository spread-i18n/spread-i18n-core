package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.excel.rows
import com.andro.spreadi18ncore.excel.skipTo
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.nio.file.Path


internal class NewExcelFile private constructor(private val filePath: Path) {

    private val workbook: XSSFWorkbook by lazy {
        XSSFWorkbook()
    }

    private val sheet: XSSFSheet by lazy {
        workbook.createSheet()
    }

    companion object {
        fun onPath(filePath: Path): NewExcelFile {
            return NewExcelFile(filePath)
        }
    }

    fun load(content: String): NewExcelFile {
        content.lineSequence()
            .map { it.trim() }
            .filter { it.startsWith("│") }
            .forEach { rowContent ->
                sheet.createRow(sheet.rows.count()).apply {
                    rowContent
                        .split("│")
                        .asSequence()
                        .skipTo(1)
                        .map { it.trim() }
                        .withIndex()
                        .forEach { indexedCellContent ->
                            with(indexedCellContent) {
                                createCell(index).setCellValue(value)
                            }
                        }
                }
            }
        return this
    }

    fun save() {
        workbook.use {
            val outputStream = FileOutputStream(filePath.toFile())
            workbook.write(outputStream)
        }
    }

    fun writeRow(vararg values: String) {
        sheet.createRow(sheet.rows.count()).apply {
            values.withIndex().forEach {
                createCell(it.index).setCellValue(it.value)
            }
        }
    }
}