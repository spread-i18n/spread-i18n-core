package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.sourcetargetmatching.AndroidSourceTargetMatcher
import com.andro.spreadi18ncore.sourcetargetmatching.SourceTargetMatcher
import com.andro.spreadi18ncore.sourcetargetmatching.TargetDirectory
import com.andro.spreadi18ncore.sourcesheet.TranslationKeyType
import com.andro.spreadi18ncore.filewriting.AndroidTranslationFileWriter
import com.andro.spreadi18ncore.filewriting.TranslationFileWriter
import com.andro.spreadi18ncore.filewriting.iOSTranslationFileWriter
import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.sourcetargetmatching.iOSSourceTargetMatcher
import com.andro.spreadi18ncore.valuetransformation.AndroidDefaultValueTransformation
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import com.andro.spreadi18ncore.valuetransformation.iOSDefaultValueTransformation
import java.io.File
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
        ImportException("Any supported project type not found in: ${projectPath}.")

internal class TargetProject(private val projectPath: Path) {
    val type: ProjectType = ProjectType.values().firstOrNull { it.existsIn(projectPath) }
            ?: throw SupportedProjectTypeNotFound(projectPath)

    val localizationDirectories: List<TargetDirectory> by lazy {
        type.localizationDirectoriesFinder.findLocalizationDirectoriesIn(projectPath.toFile())
    }
}

internal enum class ProjectType {
    iOS {
        override val sourceTargetMatcher =
            iOSSourceTargetMatcher()

        override fun fileWriter(path: Path) = iOSTranslationFileWriter(path)

        override val translationKeyType = TranslationKeyType.iOS

        override fun existsIn(path: Path) =
            xcodeprojDirectory.existsIn(path)

        override val localizationDirectoriesFinder =
            iOSLocalizationDirectoriesFinder()

        override val valueTransformation = iOSDefaultValueTransformation()
    },
    Android {
        override val sourceTargetMatcher =
            AndroidSourceTargetMatcher()

        override fun fileWriter(path: Path) = AndroidTranslationFileWriter(path)

        override val translationKeyType = TranslationKeyType.Android

        override fun existsIn(path: Path) =
            AndroidManifest.existsIn(path)

        override val localizationDirectoriesFinder =
            AndroidLocalizationDirectoriesFinder()

        override val valueTransformation = AndroidDefaultValueTransformation()
    };
    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val localizationDirectoriesFinder: LocalizationDirectoriesFinder
    abstract val valueTransformation: ValueTransformation
    abstract fun fileWriter(path: Path): TranslationFileWriter
    abstract val translationKeyType: TranslationKeyType
    abstract fun existsIn(path: Path): Boolean
}


@Suppress("ClassName")
object xcodeprojDirectory {
    fun existsIn(path: Path): Boolean {
        var isXcodeDir: (File)->Boolean = {
            it.name.endsWith(".xcodeproj")
        }
        return (path.toFile().dirs.any{isXcodeDir(it)}) ||
                allDirsRecursively(path.toFile()).any { dir -> dir.dirs.any {isXcodeDir(it) }}
    }
}

object AndroidManifest {
    fun existsIn(path: Path): Boolean {
        return allDirsRecursively(path.toFile())
                .any { dir -> dir.files.any { it.name == "AndroidManifest.xml"}  }
    }
}

