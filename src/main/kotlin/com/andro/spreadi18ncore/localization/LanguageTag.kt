package com.andro.spreadi18ncore.localization

import com.andro.spreadi18ncore.excel.ImportException
import java.nio.file.Path

class LanguageTagExtractionError(name: String) : ImportException("Can not extract language tag from: $name")

internal data class LanguageTag private constructor(val canonical: String) {

    companion object {
        fun extractFromPath(path: Path):LanguageTag {
            val name = path.toFile().name
            if (name == "values" || name == "Base.lproj") {
                return LanguageTag("en")
            } else if (name.startsWith("values-")) {
                return extractFromString(name.removePrefix("values-"))
            } else if (name.endsWith(".lproj")) {
                return extractFromString(name.removeSuffix(".lproj"))
            }
            throw LanguageTagExtractionError(name)
        }

        private val languageTagRegex = Regex("""^([a-z]{2})-?r?([A-Z]{2})?$""")

        fun extractFromString(tagCandidate: String): LanguageTag {
            fun extract(tagCandidate: String): String? {
                return languageTagRegex.matchEntire(tagCandidate)?.groups?.filterNotNull()?.let { group ->
                    when(group.size) {
                        2 -> group[1].value
                        3 -> "${group[1].value}-${group[2].value.toUpperCase()}"
                        else -> null
                    }
                }
            }
            extract(tagCandidate)?.let { tag ->
                return LanguageTag(tag)
            } ?: throw LanguageTagExtractionError(tagCandidate)
        }
    }
}
