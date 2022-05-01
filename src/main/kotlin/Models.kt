import java.nio.file.Path

fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)

open class ImportException(message: String): Exception(message)

internal data class ImportEvaluation(val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets)

internal enum class ProjectType {
    iOS {
        override val sourceTargetMatcher: SourceTargetMatcher
            get() = iOSSourceTargetMatcher()

        override fun fileWriter(path: Path): TranslationFileWriter {
            return iOSFileWriter(path)
        }

        override val translationKeyType: TranslationKeyType
            get() = TranslationKeyType.iOS

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

        override val directoryFinder: LocalizationDirFinder
            get() = AndroidLocalizationDirFinder()
    };
    abstract val sourceTargetMatcher: SourceTargetMatcher
    abstract val directoryFinder: LocalizationDirFinder
    internal abstract fun fileWriter(path: Path): TranslationFileWriter
    //TODO: propose better name
    abstract val translationKeyType: TranslationKeyType
}
