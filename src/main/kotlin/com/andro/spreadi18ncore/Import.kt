package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.importing.Importer
import com.andro.spreadi18ncore.targetproject.TargetProject
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


object Import {

    fun perform(sourceFilePath: Path, targetProjectPath: Path) {
        try {
            workbook(sourceFilePath).use {
                val importer = Importer(
                    it.getSheetAt(0),
                    project(targetProjectPath)
                )
                importer.import()
            }
        } catch (exc: ImportException) {
            throw exc
        } catch (exc: Exception) {
            throw UnknownImportError(exc)
        }
    }

    private fun workbook(sourceFilePath: Path): XSSFWorkbook {
        try {
            val file = FileInputStream(sourceFilePath.toFile())
            return XSSFWorkbook(file)
        } catch (exc: Exception) {
            throw WorkbookOpeningError(exc)
        }
    }

    private fun project(targetProjectPath: Path): TargetProject {
        return TargetProject(targetProjectPath)
    }
}

internal class UnknownImportError(exc: Exception): ImportException(cause = exc) {}
internal class WorkbookOpeningError(exc: Exception): ImportException(cause = exc) {}
