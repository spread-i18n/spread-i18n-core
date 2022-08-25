package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.ExcelTranslationTableWriter
import com.andro.spreadi18ncore.export.ProjectTranslationTableReader
import com.andro.spreadi18ncore.export.TranslationKeyValueReader
import com.andro.spreadi18ncore.filewriting.ExcelTranslationTableReader
import com.andro.spreadi18ncore.filewriting.TranslationKeyValueWriter
import com.andro.spreadi18ncore.importing.ProjectTranslationTableWriter
import com.andro.spreadi18ncore.sourcesheet.ImportException
import com.andro.spreadi18ncore.sourcesheet.ValueTransformations
import com.andro.spreadi18ncore.targetproject.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


internal interface TranslationsSource {
    val translationTableReader: TranslationTableReader
}

internal class ProjectTranslations(private val project: Project) : TranslationsSource, TranslationsDestination {

    override val translationTableReader: TranslationTableReader
        get() = ProjectTranslationTableReader(project)

    override val translationTableWriter: TranslationTableWriter
        get() = ProjectTranslationTableWriter(project)
}

internal interface TranslationsDestination {
    val translationTableWriter: TranslationTableWriter
}

internal class ExcelFile(private val filePath: Path, private val type: ProjectType) :
    TranslationsSource, TranslationsDestination {

    override val translationTableWriter: TranslationTableWriter
        get() = ExcelTranslationTableWriter(filePath)

    override val translationTableReader: TranslationTableReader
        get() = ExcelTranslationTableReader(filePath, type)
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

    private val type: ProjectType =
        ProjectType.values().firstOrNull { it.existsIn(projectPath) } ?: throw SupportedProjectTypeNotFound(projectPath)

    internal val localizationFiles: List<LocalizationFile> by lazy {
        type.localizationFileFinder.findLocalizationFileIn(projectPath.toFile())
    }

    fun export(to: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(to, to = { destinationFilePath ->
            val project = ProjectTranslations(this)
            val excelFile = ExcelFile(destinationFilePath, type)
            Transfer.from(project).to(excelFile)
        })
    }

    fun import(from: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(from, to = { sourceFilePath ->
            val excelFile = ExcelFile(sourceFilePath, type)
            val project = ProjectTranslations(this)
            Transfer.from(excelFile).to(project)
        })
    }

    internal fun keyValueReader(localizationFile: LocalizationFile): TranslationKeyValueReader =
        type.keyValueReader(localizationFile.path)

    internal fun keyValueWriter(localizationFile: LocalizationFile): TranslationKeyValueWriter =
        type.keyValueWriter(localizationFile.path)
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

internal inline fun <T, R> rename(obj: T, to: (T) -> R): R {
    return to(obj)
}
internal inline fun <R> tryBlock(block: () -> R): R =
    try {
        block()
    } catch (exc: Exception) {
        throw UnknownTransferError(exc)
    }

internal object Transfer {
    fun from(source: TranslationsSource): TransferPerformer {
        return TransferPerformer(source)
    }

    internal class TransferPerformer(private val source: TranslationsSource) {
        fun to(destination: TranslationsDestination) {
            val table = source.translationTableReader.read()
            destination.translationTableWriter.write(table)
        }
    }
}
