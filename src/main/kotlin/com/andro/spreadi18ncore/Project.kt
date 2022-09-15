package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.excel.ValueTransformations
import com.andro.spreadi18ncore.localization.LocalizationFile
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.project.SupportedProjectTypeNotFound
import com.andro.spreadi18ncore.transfer.Transfer
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.rename
import com.andro.spreadi18ncore.transfer.translation.ExcelFileDestination
import com.andro.spreadi18ncore.transfer.translation.ExcelFileSource
import com.andro.spreadi18ncore.transfer.translation.ProjectTranslationsDestination
import com.andro.spreadi18ncore.transfer.translation.ProjectTranslationsSource
import com.andro.spreadi18ncore.transfer.tryBlock
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

    private val type: ProjectType =
        ProjectType.values().firstOrNull { it.existsIn(projectPath) } ?: throw SupportedProjectTypeNotFound(projectPath)

    internal val localizationFiles: List<LocalizationFile> by lazy {
        type.localizationFileFinder.findLocalizationFilesIn(projectPath.toFile())
    }

    fun export(to: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(to, to = { destinationFilePath ->
            val project = ProjectTranslationsSource(this, valueTransformations)
            val excelFile = ExcelFileDestination(destinationFilePath, type)
            Transfer.from(project).to(excelFile)
        })
    }

    fun import(from: Path, valueTransformations: ValueTransformations? = null) = tryBlock {
        rename(from, to = { sourceFilePath ->
            val excelFile = ExcelFileSource(sourceFilePath, type, valueTransformations)
            val project = ProjectTranslationsDestination(this)
            Transfer.from(excelFile).to(project)
        })
    }

    internal fun keyValueReader(localizationFile: LocalizationFile): TranslationKeyValueReader =
        type.keyValueReader(localizationFile.path)

    internal fun keyValueWriter(localizationFile: LocalizationFile): TranslationKeyValueWriter =
        type.keyValueWriter(localizationFile.path)
}
