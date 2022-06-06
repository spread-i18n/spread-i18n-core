package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.importing.Importer
import com.andro.spreadi18ncore.targetproject.TargetProject
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path

data class ImportConfiguration(
        val sourceFilePath: Path,
        val targetProjectPath: Path,
        val valueTransformationMap: Map<String, String>? = null,
)

object Import {

    @JvmStatic
    fun perform(configuration: ImportConfiguration) {
        try {
            workbook(configuration.sourceFilePath).use {
                val importer = Importer(
                        it.getSheetAt(0),
                        project(configuration.targetProjectPath),
                        configuration.valueTransformationMap
                )
                importer.import()
            }
        } catch (exc: ImportException) {
            throw exc
        } catch (exc: Exception) {
            throw UnknownImportError(exc)
        }
    }

    @JvmStatic
    fun perform(sourceFilePath: Path, targetProjectPath: Path) {
        perform(ImportConfiguration(sourceFilePath, targetProjectPath))
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
