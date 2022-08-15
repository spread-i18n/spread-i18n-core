package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.*
import com.andro.spreadi18ncore.export.TranslationFileReader
import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.importing.Importer
import com.andro.spreadi18ncore.importing.ValueTransformations
import com.andro.spreadi18ncore.targetproject.*
import com.andro.spreadi18ncore.targetproject.LocalizationDirectory
import com.andro.spreadi18ncore.targetproject.LocalizationFile
import com.andro.spreadi18ncore.targetproject.ProjectTranslationTableReader
import com.andro.spreadi18ncore.targetproject.ProjectType
import com.andro.spreadi18ncore.targetproject.SupportedProjectTypeNotFound
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path

internal interface TranslationsSource {
    val translationTableReader:TranslationTableReader
}
internal class ProjectTranslations(private val project: Project): TranslationsSource {
    override val translationTableReader: TranslationTableReader
        get() = ProjectTranslationTableReader(project)
}

internal interface TranslationsDestination {
    val translationTableWriter: TranslationTableWriter
}

internal class ExcelTranslations(private val filePath: Path): TranslationsDestination {
    override val translationTableWriter: TranslationTableWriter
        get() = ExcelTranslationTableWriter(filePath)
}
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

    internal val localizationDirectories: List<LocalizationDirectory> by lazy {
        type.localizationResourceFinder.findLocalizationDirectoriesIn(projectPath.toFile())
    }

    internal val localizationFiles: List<LocalizationFile> by lazy {
        type.localizationResourceFinder.findLocalizationFilesIn(projectPath.toFile())
    }

    fun export(destinationFilePath: Path, valueTransformations: ValueTransformations? = null) {
        try {
            val projectTranslations = ProjectTranslations(this)
            val excelTranslations = ExcelTranslations(destinationFilePath)
            transfer(from = projectTranslations, to = excelTranslations)
        } catch (exc: Exception) {
            throw UnknownTransferError(exc)
        }
    }
    internal fun transfer(source: TranslationsSource,
                          destination: TranslationsDestination) {

        val table = source.translationTableReader.read()
        destination.translationTableWriter.write(table)
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
            throw UnknownTransferError(exc)
        }
    }

    internal fun translationFileReader(localizationDirectory: LocalizationDirectory): TranslationFileReader =
        type.fileReader(localizationDirectory)
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
internal class UnknownTransferError(exc: Exception) : ImportException(cause = exc)
internal class WorkbookOpeningError(exc: Exception) : ImportException(cause = exc)

private fun Project.transfer(from: TranslationsSource,
                     to: TranslationsDestination) {
    transfer(source = from, destination = to)
}