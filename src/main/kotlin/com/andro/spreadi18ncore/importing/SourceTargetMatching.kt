package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.sourcesheet.LocaleCell
import com.andro.spreadi18ncore.targetproject.LocaleValue
import com.andro.spreadi18ncore.targetproject.LocaleValueExtractor
import com.andro.spreadi18ncore.targetproject.LocalizationDirectory
import java.text.DateFormat
import java.util.*

internal interface SourceTargetMatcher {
    fun match(sources: Collection<LocaleCell>, targets: Collection<LocalizationDirectory>):
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

internal class Locales private constructor() {

    fun findLocale(localeDataCandidate: String): Locale? {
        return items.find { locale -> locale.identifiedBy(localeDataCandidate) }
    }

    companion object {
        val allLocales = Locales()
    }

    val items: Array<Locale> by lazy {
        DateFormat.getAvailableLocales()
    }
}


internal data class MatchedSourceAndTarget(
    val sourceLocaleCell: LocaleCell,
    val targetDirectory: LocalizationDirectory
)//TransactionAddress, TransferAddress

internal class MatchedSourcesAndTargets(private val _matches: MutableList<MatchedSourceAndTarget> = mutableListOf()) :
    Iterable<MatchedSourceAndTarget> by _matches {

    val matches: List<MatchedSourceAndTarget> = _matches

    val count: Int
        get() = _matches.count()

    fun add(matchedSourceAndTarget: MatchedSourceAndTarget) {
        _matches.add(matchedSourceAndTarget)
    }

    fun notContainsSource(sourceLocaleCell: LocaleCell): Boolean {
        return !containsSource(sourceLocaleCell)
    }

    private fun containsSource(sourceLocaleCell: LocaleCell): Boolean {
        return _matches.find { address -> address.sourceLocaleCell.columnIndex == sourceLocaleCell.columnIndex } != null
    }

    fun notContainsTarget(target: LocalizationDirectory): Boolean {
        return !containsTarget(target)
    }

    private fun containsTarget(target: LocalizationDirectory): Boolean {
        return _matches.find { address -> address.targetDirectory.path == target.path } != null
    }

    fun getAt(index: Int): MatchedSourceAndTarget {
        return _matches[index]
    }
}

@Suppress("ClassName")
internal class iOSSourceTargetMatcher :
    SourceTargetMatcher {

    private fun matchesStrongly(source: LocaleCell, target: LocalizationDirectory): Boolean {
        val sourceTag = source.text.normalizedTag
        val targetTag = target.file.nameWithoutExtension.normalizedTag
        return sourceTag == targetTag
    }

    private fun matchesWeakly(source: LocaleCell, target: LocalizationDirectory): Boolean {
        val name = target.file.nameWithoutExtension.normalizedTag
        val locale = source.locales.find { locale -> locale.toLanguageTag().normalizedTag == name }
        return locale != null
    }

    private val LocalizationDirectory.isBaseDir: Boolean
        get() = this.file.name.toLowerCase().endsWith("base.lproj")


    override fun match(
        sources: Collection<LocaleCell>,
        targets: Collection<LocalizationDirectory>
    ): MatchedSourcesAndTargets {
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

        if ((remainingSources.size == 1) && (remainingTargets.size == 1) && remainingTargets[0].isBaseDir) {
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

internal object AndroidLocaleValueExtractor: LocaleValueExtractor {
    fun extract(path: String): String? {
        val directoryNameRegex = Regex(""".*values-?(\w{2})?-?r?(\w{2,3})?""")
        return directoryNameRegex.matchEntire(path)?.groups?.filterNotNull()?.let {
            when (it.size) {
                3 -> "${it[1].value}-${it[2].value}"
                2 -> it[1].value
                else -> null
            }
        }
    }
    override fun extract(localizationDirectory: LocalizationDirectory): LocaleValue {
        return LocaleValue(extract(localizationDirectory.path.toString()) ?: "")
    }
}

@Suppress("ClassName")
internal object iOSLocaleValueExtractor: LocaleValueExtractor {
    override fun extract(localizationDirectory: LocalizationDirectory): LocaleValue {
        return LocaleValue(localizationDirectory.file.nameWithoutExtension)
    }
}

//https://stackoverflow.com/questions/13693209/android-localization-values-folder-names
//https://developer.android.com/reference/java/util/Locale
//https://datatracker.ietf.org/doc/html/rfc4647#section-3.4.1
internal class AndroidSourceTargetMatcher :
    SourceTargetMatcher {

    companion object {
    }

    private val String.extractedTag: String? get() = AndroidLocaleValueExtractor.extract(this)

    private fun matchesStrongly(source: LocaleCell, target: LocalizationDirectory): Boolean {
        return target.file.name.extractedTag?.normalizedTag?.let { targetTag ->
            val sourceTag = source.text.normalizedTag
            sourceTag == targetTag
        } ?: false
    }

    private fun matchesWeakly(source: LocaleCell, target: LocalizationDirectory): Boolean {
        return target.file.name.extractedTag?.normalizedTag?.let { targetTag ->
            val locale = source.locales.find { locale -> locale.toLanguageTag().normalizedTag == targetTag }
            locale != null
        } ?: false
    }

    private val LocalizationDirectory.isBaseDir: Boolean
        get() = this.file.name == "values"


    override fun match(
        sources: Collection<LocaleCell>,
        targets: Collection<LocalizationDirectory>
    ): MatchedSourcesAndTargets {
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

        if ((remainingSources.size == 1) && (remainingTargets.size == 1) && remainingTargets[0].isBaseDir) {
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