package internal

import java.io.File
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
        ImportException("Any supported project type not found in: ${projectPath}.")

internal class TargetProject(private val projectPath: Path) {
    val type: ProjectType = ProjectType.values().firstOrNull { it.existsInPath(projectPath) }
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
            return iOSFileWriter(path)
        }

        override val translationKeyType: TranslationKeyType
            get() = TranslationKeyType.iOS

        override fun existsInPath(path: Path) = xcodeprojDirectory.existsInDirectory(directory = path.toFile())

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

        override fun existsInPath(path: Path) = false

        override val localizationDirectoriesFinder: LocalizationDirectoriesFinder
            get() = AndroidLocalizationDirectoriesFinder()
    };
    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val localizationDirectoriesFinder: LocalizationDirectoriesFinder
    abstract fun fileWriter(path: Path): TranslationFileWriter
    //TODO: propose better name
    abstract val translationKeyType: TranslationKeyType
    abstract fun existsInPath(path: Path): Boolean
}


@Suppress("ClassName")
internal class xcodeprojDirectory {
    companion object {
        fun existsInDirectory(directory: File): Boolean {
            val xcodeprojDirectoryCandidates = directory.listFiles { file -> file.name.endsWith(".xcodeproj") }
            return xcodeprojDirectoryCandidates?.let { it.size==1 } ?: false
        }
    }
}