package com.andro.spreadi18ncore.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


internal class WorkbookOpeningError(exc: Exception) : TransferException(cause = exc)

internal val XSSFWorkbook.firstSheet get() = this.getSheetAt(0)

internal fun workbook(sourceFilePath: Path): XSSFWorkbook {
    try {
        val file = FileInputStream(sourceFilePath.toFile())
        return XSSFWorkbook(file)
    } catch (exc: Exception) {
        throw WorkbookOpeningError(exc)
    }
}