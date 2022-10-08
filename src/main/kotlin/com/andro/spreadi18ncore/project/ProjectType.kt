package com.andro.spreadi18ncore.project

import com.andro.spreadi18ncore.transfer.exporting.AndroidTranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.exporting.iOSTranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.importing.AndroidTranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.importing.iOSTranslationKeyValueWriter
import com.andro.spreadi18ncore.excel.KeyType
import com.andro.spreadi18ncore.transfer.TransferException
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
    TransferException("Any supported project type not found in: ${projectPath}.")

internal enum class ProjectType {
    @Suppress("EnumEntryName")
    iOS {
        override fun keyValueWriter(pathOfLocalizationFile: Path) =
            iOSTranslationKeyValueWriter(pathOfLocalizationFile)

        override fun keyValueReader(pathOfLocalizationFile: Path) =
            iOSTranslationKeyValueReader(pathOfLocalizationFile)

        override val keyType = KeyType.iOS

        override fun existsIn(path: Path) = pbxprojFile.existsIn(path)

        override val localizationFileFinder = iOSLocalizationFileFinder
    },
    Android {

        override fun keyValueWriter(pathOfLocalizationFile: Path) =
            AndroidTranslationKeyValueWriter(pathOfLocalizationFile)

        override fun keyValueReader(pathOfLocalizationFile: Path) =
            AndroidTranslationKeyValueReader(pathOfLocalizationFile)

        override val keyType = KeyType.Android

        override fun existsIn(path: Path) = AndroidManifest.existsIn(path)

        override val localizationFileFinder = AndroidLocalizationFileFinder
    };

    abstract val localizationFileFinder: LocalizationFileFinder
    abstract fun keyValueWriter(pathOfLocalizationFile: Path): TranslationKeyValueWriter
    abstract fun keyValueReader(pathOfLocalizationFile: Path): TranslationKeyValueReader
    abstract val keyType: KeyType
    abstract fun existsIn(path: Path): Boolean
}
