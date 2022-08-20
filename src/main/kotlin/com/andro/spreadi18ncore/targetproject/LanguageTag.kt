package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.sourcesheet.ImportException
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
                return languageTagRegex.matchEntire(tagCandidate)?.groups?.filterNotNull()?.let { groups ->
                    when(groups.size) {
                        2 -> groups[1].value
                        3 -> "${groups[1].value}-${groups[2].value.toUpperCase()}"
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
