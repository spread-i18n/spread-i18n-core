package internal

import java.io.File
import java.nio.file.Path

internal class SupportedProjectTypeNotFound(projectPath: Path) :
        ImportException("Any supported project type not found in: ${projectPath}.")

internal class TargetProject(private val projectPath: Path) {
    val type: ProjectType = ProjectType.values().firstOrNull { it.existsInPath(projectPath) }
            ?: throw SupportedProjectTypeNotFound(projectPath)

    val localizationDirectories: List<TargetDirectory> by lazy {
        type.directoryFinder.findLocalizationDirectoriesIn(projectPath.toFile())
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

        override val directoryFinder: LocalizationDirFinder
            get() = iOSLocalizationDirFinder()
    },
    Android {
        override val sourceTargetMatcher: SourceTargetMatcher
            get() = TODO("Not yet implemented")

        override fun fileWriter(path: Path): TranslationFileWriter {
            TODO("Not yet implemented")
        }

        override val translationKeyType: TranslationKeyType
            get() = TranslationKeyType.Android

        override fun existsInPath(path: Path) = false

        override val directoryFinder: LocalizationDirFinder
            get() = AndroidLocalizationDirFinder()
    };
    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val directoryFinder: LocalizationDirFinder
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