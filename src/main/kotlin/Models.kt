import Locales.Companion.allLocales
import java.io.Closeable
import java.io.File
import java.nio.file.Path
import java.text.DateFormat
import java.util.*

fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)

fun Locale.identifiedBy(localeDataCandidate: String): Boolean {
    return when (localeDataCandidate.toLowerCase()) {
        this.country.toLowerCase() -> true
        this.displayCountry.toLowerCase() -> true
        this.language.toLowerCase() -> true
        this.displayLanguage.toLowerCase() -> true
        this.toLanguageTag().toLowerCase() -> true
        else -> false
    }
}

class Locales {

    fun findLocale(localeDataCandidate: String): Locale? {
        return items.find { locale -> locale.identifiedBy(localeDataCandidate) }
    }

    private constructor()

    companion object {
        val allLocales = Locales()
    }

    val items: Array<Locale> by lazy {
        DateFormat.getAvailableLocales()
    }
}

typealias IndexedCellValue<T> = IndexedValue<T>

class ImportException(message: String): Exception(message)

data class SourceColumn(val title: String, val column: Int) {//source point
    val locales: List<Locale> by lazy {
        allLocales.items.filter { locale -> locale.identifiedBy(title) }
    }
}

data class TargetDirectory(val file: File){
    val path: Path = file.toPath()
}

data class ImportEvaluation(val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets)
interface TranslationFileWriter: Closeable {
    fun write(key: String, value: String)
}

enum class ProjectType {
    iOS {
        override val matcher: SourceTargetMatcher
            get() = iOSSourceTargetMatcher()

        override fun fileWriter(path: Path): TranslationFileWriter {
            return iOSFileWriter(path)
        }

    },
    Android {
        override val matcher: SourceTargetMatcher
            get() = TODO("Not yet implemented")

        override fun fileWriter(path: Path): TranslationFileWriter {
            TODO("Not yet implemented")
        }
    };
    abstract val matcher: SourceTargetMatcher
    abstract fun fileWriter(path: Path): TranslationFileWriter
}

enum class ProjectKey {
    iOS {
        override val identifiers: List<String>
            get() = listOf("ios")
    },
    Android {
        override val identifiers: List<String>
            get() = listOf("android")
    },
    general {
        override val identifiers: List<String>
            get() = listOf("key", "identifier", "id")
    };
    abstract val identifiers: List<String>
}

data class MatchedSourceAndTarget(val sourceColumn: SourceColumn, val targetDirectory: TargetDirectory)//TransactionAddress, TransferAddress
class MatchedSourcesAndTargets(private val _matches: MutableList<MatchedSourceAndTarget> = mutableListOf())
    : Iterable<MatchedSourceAndTarget> by _matches {

    val matches: List<MatchedSourceAndTarget> = _matches

    val count: Int
        get() = _matches.count()

    fun add(matchedSourceAndTarget: MatchedSourceAndTarget) {
        _matches.add(matchedSourceAndTarget)
    }

    fun notContainsSource(sourceColumn: SourceColumn): Boolean {
        return !containsSource(sourceColumn)
    }
    private fun containsSource(sourceColumn: SourceColumn): Boolean {
        return _matches.find { address -> address.sourceColumn.column==sourceColumn.column } != null
    }

    fun notContainsTarget(target: TargetDirectory): Boolean {
        return !containsTarget(target)
    }
    private fun containsTarget(target: TargetDirectory): Boolean {
        return _matches.find { address -> address.targetDirectory.file.toPath()==target.file.toPath() } != null
    }

    fun getAt(index: Int): MatchedSourceAndTarget {
        return _matches[index]
    }
}
data class ImportConfiguration(val keyColumn: Int,
                               val firstTranslationRow: Int,
                               val matchedSourcesAndTargets: MatchedSourcesAndTargets,
                               val projectType: ProjectType)
