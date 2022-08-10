package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.importing.Importer
import com.andro.spreadi18ncore.importing.ValueTransformations
import com.andro.spreadi18ncore.importing.TargetDirectory
import com.andro.spreadi18ncore.targetproject.ProjectType
import com.andro.spreadi18ncore.targetproject.SupportedProjectTypeNotFound
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path

class Project private constructor(private val projectPath: Path) {

    companion object {
        fun onPath(projectPath: Path): Project {
            return Project(projectPath)
        }

        fun onPath(projectPath: String): Project {
            return onPath(Path.of(projectPath))
        }
    }

    internal val type: ProjectType = ProjectType.values().firstOrNull { it.existsIn(projectPath) }
        ?: throw SupportedProjectTypeNotFound(projectPath)

    internal val localizationDirectories: List<TargetDirectory> by lazy {
        type.localizationDirectoriesFinder.findLocalizationDirectoriesIn(projectPath.toFile())
    }

    fun import(sourceFilePath: Path, valueTransformations: ValueTransformations? = null) {
        try {
            workbook(sourceFilePath).use { workbook ->
                Importer(
                    sheet = workbook.firstSheet,
                    project = this,
                    valueTransformations = valueTransformations
                ).import()
            }
        } catch (exc: ImportException) {
            throw exc
        } catch (exc: Exception) {
            throw UnknownImportError(exc)
        }
    }
}

internal val XSSFWorkbook.firstSheet get() = this.getSheetAt(0)
internal fun workbook(sourceFilePath: Path): XSSFWorkbook {
    try {
        val file = FileInputStream(sourceFilePath.toFile())
        return XSSFWorkbook(file)
    } catch (exc: Exception) {
        throw WorkbookOpeningError(exc)
    }
}
internal class UnknownImportError(exc: Exception) : ImportException(cause = exc)
internal class WorkbookOpeningError(exc: Exception) : ImportException(cause = exc)
