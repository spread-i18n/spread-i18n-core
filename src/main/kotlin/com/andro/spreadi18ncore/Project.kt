package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.localization.LocalizationFile
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.project.SupportedProjectTypeNotFound
import com.andro.spreadi18ncore.transfer.*
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.rename
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformations
import com.andro.spreadi18ncore.transfer.translation.ExcelFileDestination
import com.andro.spreadi18ncore.transfer.translation.ExcelFileSource
import com.andro.spreadi18ncore.transfer.translation.ProjectTranslationsDestination
import com.andro.spreadi18ncore.transfer.translation.ProjectTranslationsSource
import com.andro.spreadi18ncore.transfer.tryBlock
import java.nio.file.Path

@Suppress("unused")
class Project private constructor(private val projectPath: Path) {

    companion object {
        @JvmStatic
        @Throws(TransferException::class)
        fun onPath(projectPath: Path): Project {
            return Project(projectPath)
        }

        @JvmStatic
        @Throws(TransferException::class)
        fun onPath(projectPath: String): Project {
            return onPath(Path.of(projectPath))
        }
    }

    private val type: ProjectType =
        ProjectType.values().firstOrNull { it.existsIn(projectPath) } ?: throw SupportedProjectTypeNotFound(projectPath)

    internal val localizationFiles: List<LocalizationFile> by lazy {
        type.localizationFileFinder.findLocalizationFilesIn(projectPath.toFile())
    }


    @Throws(TransferException::class)
    fun exportTo(destinationFilePath: String, valueTransformations: ValueTransformations? = null) =
        export(to = Path.of(destinationFilePath), valueTransformations)

    @Throws(TransferException::class)
    fun exportTo(destinationFilePath: Path, valueTransformations: ValueTransformations? = null) =
        export(to = destinationFilePath, valueTransformations)

    @Throws(TransferException::class)
    fun export(destinationFilePath: String, valueTransformations: ValueTransformations? = null) =
        export(to = Path.of(destinationFilePath), valueTransformations)

    @Throws(TransferException::class)
    fun export(to: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(to, to = { destinationFilePath ->
            val projectTranslations = ProjectTranslationsSource(this, valueTransformations)
            val excelFile = ExcelFileDestination(destinationFilePath, type)
            transfer(from = projectTranslations, to = excelFile)
        })
    }


    @Throws(TransferException::class)
    fun importFrom(sourceFilePath: String, valueTransformations: ValueTransformations? = null) =
        importFrom(Path.of(sourceFilePath), valueTransformations)

    @Throws(TransferException::class)
    fun importFrom(sourceFilePath: Path, valueTransformations: ValueTransformations? = null) =
        import(sourceFilePath, valueTransformations)

    @Throws(TransferException::class)
    fun import(sourceFilePath: String, valueTransformations: ValueTransformations? = null) =
        import(from = Path.of(sourceFilePath), valueTransformations)

    @Throws(TransferException::class)
    fun import(from: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(from, to = { sourceFilePath ->
            val excelFile = ExcelFileSource(sourceFilePath, type, valueTransformations)
            val projectTranslations = ProjectTranslationsDestination(this)
            transfer(from = excelFile, to = projectTranslations)
        })
    }

    internal fun keyValueReader(localizationFile: LocalizationFile): TranslationKeyValueReader =
        type.keyValueReader(localizationFile.path)

    internal fun keyValueWriter(localizationFile: LocalizationFile): TranslationKeyValueWriter =
        type.keyValueWriter(localizationFile.path)
}
