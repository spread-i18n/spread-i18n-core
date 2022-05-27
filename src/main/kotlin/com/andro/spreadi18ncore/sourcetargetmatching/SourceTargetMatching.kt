package com.andro.spreadi18ncore.sourcetargetmatching

import com.andro.spreadi18ncore.sourcetargetmatching.Locales.Companion.allLocales
import java.io.File
import java.nio.file.Path
import java.text.DateFormat
import java.util.*

internal interface SourceTargetMatcher {
    fun match(sources: Collection<SourceColumn>, targets: Collection<TargetDirectory>):
            MatchedSourcesAndTargets
}

val String.normalizedTag: String
    get() {
        return toLowerCase().replace("_", "-")
    }

internal fun Locale.identifiedBy(localeDataCandidate: String): Boolean {
    return when (localeDataCandidate.toLowerCase()) {
        this.country.toLowerCase() -> true
        this.displayCountry.toLowerCase() -> true
        this.language.toLowerCase() -> true
        this.displayLanguage.toLowerCase() -> true
        this.toLanguageTag().toLowerCase() -> true
        else -> false
    }
}

internal class Locales {

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

internal data class SourceColumn(val title: String, val column: Int) {//source point
    val locales: List<Locale> by lazy {
        allLocales.items.filter { locale -> locale.identifiedBy(title) }
    }
}

internal data class TargetDirectory(val file: File){
    val path: Path = file.toPath()
}

internal data class MatchedSourceAndTarget(val sourceColumn: SourceColumn, val targetDirectory: TargetDirectory)//TransactionAddress, TransferAddress

internal class MatchedSourcesAndTargets(private val _matches: MutableList<MatchedSourceAndTarget> = mutableListOf())
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

@Suppress("ClassName")
internal class iOSSourceTargetMatcher:
    SourceTargetMatcher {

    private fun matchesStrongly(source: SourceColumn, target: TargetDirectory): Boolean {
        val sourceTag = source.title.normalizedTag
        val targetTag = target.file.nameWithoutExtension.normalizedTag
        return sourceTag == targetTag
    }

    private fun matchesWeakly(source: SourceColumn, target: TargetDirectory): Boolean {
        val name = target.file.nameWithoutExtension.normalizedTag
        val locale = source.locales.find { locale -> locale.toLanguageTag().normalizedTag == name }
        return locale != null
    }

    private val TargetDirectory.isBaseDir: Boolean
        get() = this.file.name.toLowerCase().endsWith("base.lproj")


    override fun match(sources: Collection<SourceColumn>, targets: Collection<TargetDirectory>): MatchedSourcesAndTargets {
        val matchedSourcesAndTargets =
            MatchedSourcesAndTargets()
        for (source in sources) {
            for (target in targets) {
                if (matchesStrongly(source, target)) {
                    matchedSourcesAndTargets.add(
                        MatchedSourceAndTarget(
                            source,
                            target
                        )
                    )
                    break
                }
            }
        }

        var remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        var remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        for (source in remainingSources) {
            for (target in remainingTargets) {
                if (matchesWeakly(source, target)) {
                    matchedSourcesAndTargets.add(
                        MatchedSourceAndTarget(
                            source,
                            target
                        )
                    )
                    break
                }
            }
        }

        remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        if ((remainingSources.size==1) && (remainingTargets.size==1) && remainingTargets[0].isBaseDir) {
            matchedSourcesAndTargets.add(
                MatchedSourceAndTarget(
                    remainingSources[0],
                    remainingTargets[0]
                )
            )
        }
        return matchedSourcesAndTargets
    }
}

//https://stackoverflow.com/questions/13693209/android-localization-values-folder-names
//https://developer.android.com/reference/java/util/Locale
//https://datatracker.ietf.org/doc/html/rfc4647#section-3.4.1
internal class AndroidSourceTargetMatcher:
    SourceTargetMatcher {

    private val String.extractedTag: String?
        get() {
            val directoryNameRegex = Regex(""".*values-?(\w{2})?-?r?(\w{2,3})?""")
            return directoryNameRegex.matchEntire(this)?.groups?.filterNotNull()?.let {
                when (it.size) {
                    3 -> "${it[1].value}-${it[2].value}"
                    2 -> "${it[1].value}"
                    else -> null
                }
            }
        }

    private fun matchesStrongly(source: SourceColumn, target: TargetDirectory): Boolean {
        return target.file.name.extractedTag?.normalizedTag?.let { targetTag ->
            val sourceTag = source.title.normalizedTag
            sourceTag == targetTag
        } ?: false
    }

    private fun matchesWeakly(source: SourceColumn, target: TargetDirectory): Boolean {
        return target.file.name.extractedTag?.normalizedTag?.let { targetTag ->
            val locale = source.locales.find { locale -> locale.toLanguageTag().normalizedTag == targetTag }
            locale != null
        } ?: false
    }

    private val TargetDirectory.isBaseDir: Boolean
        get() = this.file.name == "values"


    override fun match(sources: Collection<SourceColumn>, targets: Collection<TargetDirectory>): MatchedSourcesAndTargets {
        val matchedSourcesAndTargets =
            MatchedSourcesAndTargets()
        for (source in sources) {
            for (target in targets) {
                if (matchesStrongly(source, target)) {
                    matchedSourcesAndTargets.add(
                        MatchedSourceAndTarget(
                            source,
                            target
                        )
                    )
                    break
                }
            }
        }

        var remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        var remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        for (source in remainingSources) {
            for (target in remainingTargets) {
                if (matchesWeakly(source, target)) {
                    matchedSourcesAndTargets.add(
                        MatchedSourceAndTarget(
                            source,
                            target
                        )
                    )
                    break
                }
            }
        }

        remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        if ((remainingSources.size==1) && (remainingTargets.size==1) && remainingTargets[0].isBaseDir) {
            matchedSourcesAndTargets.add(
                MatchedSourceAndTarget(
                    remainingSources[0],
                    remainingTargets[0]
                )
            )
        }
        return matchedSourcesAndTargets
    }
}