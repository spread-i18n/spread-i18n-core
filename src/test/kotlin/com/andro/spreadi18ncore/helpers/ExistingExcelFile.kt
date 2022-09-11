package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.excel.firstSheet
import com.andro.spreadi18ncore.excel.rows
import com.andro.spreadi18ncore.excel.workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.Closeable
import java.nio.file.Path


internal class ExistingExcelFile private constructor(filePath: Path) : Closeable {

    private val workbook: XSSFWorkbook by lazy {
        workbook(filePath)
    }

    fun containsInRow(vararg values: String): Boolean {
        workbook.firstSheet.rows.iterator().forEach { row ->
            var numberOfValuesFound = 0
            row.cellIterator().forEach { cell ->
                cell.stringCellValue?.let { cellValue ->
                    values.toList().find { it == cellValue }?.let { numberOfValuesFound += 1 }
                }
            }
            if (numberOfValuesFound == values.count()) {
                return true
            }
        }
        return false
    }

    companion object {
        fun onPath(filePath: Path): ExistingExcelFile {
            return ExistingExcelFile(filePath)
        }
    }

    override fun close() {
        workbook.close()
    }
}