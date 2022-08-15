package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.export.AndroidTranslationFileReader
import com.andro.spreadi18ncore.export.TranslationFileReader
import com.andro.spreadi18ncore.export.iOSTranslationFileReader
import com.andro.spreadi18ncore.sourcesheet.TranslationKeyType
import com.andro.spreadi18ncore.filewriting.AndroidTranslationFileWriter
import com.andro.spreadi18ncore.filewriting.TranslationFileWriter
import com.andro.spreadi18ncore.filewriting.iOSTranslationFileWriter
import com.andro.spreadi18ncore.importing.*
import com.andro.spreadi18ncore.importing.AndroidLocaleValueExtractor
import com.andro.spreadi18ncore.importing.AndroidSourceTargetMatcher
import com.andro.spreadi18ncore.importing.SourceTargetMatcher
import com.andro.spreadi18ncore.importing.iOSSourceTargetMatcher
import com.andro.spreadi18ncore.valuetransformation.AndroidDefaultValueTransformation
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import com.andro.spreadi18ncore.valuetransformation.iOSDefaultValueTransformation
import java.io.File
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
    ImportException("Any supported project type not found in: ${projectPath}.")

internal interface LocaleValueExtractor {
    fun extract(localizationDirectory: LocalizationDirectory): LocaleValue
}

internal enum class ProjectType {
    iOS {
        override val sourceTargetMatcher =
            iOSSourceTargetMatcher()

        override fun fileWriter(path: Path) = iOSTranslationFileWriter(path)

        override fun fileReader(localizationDirectory: LocalizationDirectory) =
            iOSTranslationFileReader(localizationDirectory)

        override val translationKeyType = TranslationKeyType.iOS

        override fun existsIn(path: Path) = xcodeprojDirectory.existsIn(path)

        override val localizationResourceFinder = iOSLocalizationDirectoriesFinder()

        override val valueTransformation = iOSDefaultValueTransformation()

        override val localeValueExtractor get() = iOSLocaleValueExtractor
    },
    Android {
        override val sourceTargetMatcher =
            AndroidSourceTargetMatcher()

        override fun fileWriter(path: Path) = AndroidTranslationFileWriter(path)

        override fun fileReader(localizationDirectory: LocalizationDirectory) =
            AndroidTranslationFileReader(localizationDirectory)

        override val translationKeyType = TranslationKeyType.Android

        override fun existsIn(path: Path) = AndroidManifest.existsIn(path)

        override val localizationResourceFinder = AndroidLocalizationResourceFinder()

        override val valueTransformation = AndroidDefaultValueTransformation()

        override val localeValueExtractor = AndroidLocaleValueExtractor
    };

    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val localizationResourceFinder: LocalizationResourceFinder
    abstract val valueTransformation: ValueTransformation
    abstract fun fileWriter(path: Path): TranslationFileWriter
    abstract fun fileReader(localizationDirectory: LocalizationDirectory): TranslationFileReader
    abstract val translationKeyType: TranslationKeyType
    abstract fun existsIn(path: Path): Boolean
    abstract val localeValueExtractor: LocaleValueExtractor
}


@Suppress("ClassName")
internal object xcodeprojDirectory {
    fun existsIn(path: Path): Boolean {
        var isXcodeDir: (File) -> Boolean = {
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

