package com.andro.spreadi18ncore.localization

import com.andro.spreadi18ncore.transfer.TransferException
import java.nio.file.Path

class LanguageTagExtractionError(tagNameCandidate: String) : TransferException("Can not extract language tag from: $tagNameCandidate")

internal data class LanguageTag private constructor(val canonical: String) {

    val isDefault: Boolean by lazy {
        canonical == "default"
    }

    companion object {

        val default: LanguageTag
            get() {
                return LanguageTag("default")
            }

        val english: LanguageTag get() {
            return extractFromString("en")
        }

        fun extractFromPath(path: Path): LanguageTag {
            val fileName = path.toFile().name
            if (fileName == "values") {
                return default
            } else if (fileName.startsWith("values-")) {
                return extractFromString(fileName.removePrefix("values-"))
            } else if (fileName.endsWith(".lproj")) {
                return extractFromString(fileName.removeSuffix(".lproj"))
            }
            throw LanguageTagExtractionError(fileName)
        }

        private val languageTagRegex = Regex("""^([a-z]{2})-?r?([A-Z]{2})?$""")

        fun extractFromStringOrNull(tagCandidate: String): LanguageTag? {
            if (tagCandidate == "default") {
                return default
            }
            fun extract(tagCandidate: String): String? {
                return languageTagRegex.matchEntire(tagCandidate)?.groups?.filterNotNull()?.let { group ->
                    when (group.size) {
                        2 -> group[1].value
                        3 -> "${group[1].value}-${group[2].value.toUpperCase()}"
                        else -> null
                    }
                }
            }
            return extract(tagCandidate)?.let { tag ->
                LanguageTag(tag)
            }
        }
        fun extractFromString(tagCandidate: String): LanguageTag {
            return extractFromStringOrNull(tagCandidate) ?: throw LanguageTagExtractionError(tagCandidate)
        }
    }
}
