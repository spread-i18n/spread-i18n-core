package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.firstSheet
import com.andro.spreadi18ncore.importing.rows
import com.andro.spreadi18ncore.workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.Closeable
import java.nio.file.Path


internal class ExcelFile private constructor(filePath: Path) : Closeable {

    private val workbook: XSSFWorkbook by lazy {
        workbook(filePath)
    }

    fun contains(key: String, value: String): Boolean {
        workbook.firstSheet.rows.iterator().forEach { row ->
            var containsKey = false
            var containsValue = false
            row.cellIterator().forEach {
                if (key == it.stringCellValue) {
                    containsKey = true
                } else if (value == it.stringCellValue) {
                    containsValue = true
                }
                if (containsKey && containsValue) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        fun of(filePath: Path): ExcelFile {
            return ExcelFile(filePath)
        }
    }

    override fun close() {
        workbook.close()
    }
}