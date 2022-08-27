package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.export.AndroidTranslationKeyValueReader
import com.andro.spreadi18ncore.export.TranslationKeyValueReader
import com.andro.spreadi18ncore.export.iOSTranslationKeyValueReader
import com.andro.spreadi18ncore.filewriting.AndroidTranslationKeyValueWriter
import com.andro.spreadi18ncore.filewriting.TranslationKeyValueWriter
import com.andro.spreadi18ncore.filewriting.iOSTranslationKeyValueWriter
import com.andro.spreadi18ncore.sourcesheet.ImportException
import com.andro.spreadi18ncore.sourcesheet.TranslationKeyType
import java.io.File
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


@Suppress("ClassName")
internal object xcodeprojDirectory {
    fun existsIn(path: Path): Boolean {
        val isXcodeDir: (File) -> Boolean = {
            it.name.endsWith(".xcodeproj")
        }
        return (path.toFile().dirs.any { isXcodeDir(it) }) ||
                allDirsRecursively(path.toFile()).any { dir -> dir.dirs.any { isXcodeDir(it) } }
    }
}

internal object AndroidManifest {
    fun existsIn(path: Path): Boolean {
        return allDirsRecursively(path.toFile())
            .any { dir -> dir.files.any { it.name == "AndroidManifest.xml" } }
    }
}

