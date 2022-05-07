package com.andro.spreadi18ncore

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
        override val sourceTargetMatcher: SourceTargetMatcher
            get() = iOSSourceTargetMatcher()

        override fun fileWriter(path: Path): TranslationFileWriter {
            return iOSTranslationFileWriter(path)
        }

        override val translationKeyType: TranslationKeyType
            get() = TranslationKeyType.iOS

        override fun existsIn(path: Path) = xcodeprojDirectory.existsIn(path)

        override val localizationDirectoriesFinder: LocalizationDirectoriesFinder
            get() = iOSLocalizationDirectoriesFinder()
    },
    Android {
        override val sourceTargetMatcher: SourceTargetMatcher
            get() = AndroidSourceTargetMatcher()

        override fun fileWriter(path: Path): TranslationFileWriter {
            TODO("Not yet implemented")
        }

        override val translationKeyType: TranslationKeyType
            get() = TranslationKeyType.Android

        override fun existsIn(path: Path) = AndroidManifest.existsIn(path)

        override val localizationDirectoriesFinder: LocalizationDirectoriesFinder
            get() = AndroidLocalizationDirectoriesFinder()
    };
    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val localizationDirectoriesFinder: LocalizationDirectoriesFinder
    abstract fun fileWriter(path: Path): TranslationFileWriter
    //TODO: propose better name
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

