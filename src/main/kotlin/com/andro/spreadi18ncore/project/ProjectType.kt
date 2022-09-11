package com.andro.spreadi18ncore.project

import com.andro.spreadi18ncore.transfer.exporting.AndroidTranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.exporting.iOSTranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.importing.AndroidTranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.importing.iOSTranslationKeyValueWriter
import com.andro.spreadi18ncore.excel.ImportException
import com.andro.spreadi18ncore.excel.TranslationKeyType
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
    ImportException("Any supported project type not found in: ${projectPath}.")

internal enum class ProjectType {
    @Suppress("EnumEntryName")
    iOS {
        override fun keyValueWriter(pathOfLocalizationFile: Path) =
            iOSTranslationKeyValueWriter(pathOfLocalizationFile)

        override fun keyValueReader(pathOfLocalizationFile: Path) =
            iOSTranslationKeyValueReader(pathOfLocalizationFile)

        override val translationKeyType = TranslationKeyType.iOS

        override fun existsIn(path: Path) = xcodeprojDirectory.existsIn(path)

        override val localizationFileFinder = iOSLocalizationFileFinder()
    },
    Android {

        override fun keyValueWriter(pathOfLocalizationFile: Path) =
            AndroidTranslationKeyValueWriter(pathOfLocalizationFile)

        override fun keyValueReader(pathOfLocalizationFile: Path) =
            AndroidTranslationKeyValueReader(pathOfLocalizationFile)

        override val translationKeyType = TranslationKeyType.Android

        override fun existsIn(path: Path) = AndroidManifest.existsIn(path)

        override val localizationFileFinder = AndroidLocalizationFileFinder()
    };

    abstract val localizationFileFinder: LocalizationFileFinder
    abstract fun keyValueWriter(pathOfLocalizationFile: Path): TranslationKeyValueWriter
    abstract fun keyValueReader(pathOfLocalizationFile: Path): TranslationKeyValueReader
    abstract val translationKeyType: TranslationKeyType
    abstract fun existsIn(path: Path): Boolean
}
