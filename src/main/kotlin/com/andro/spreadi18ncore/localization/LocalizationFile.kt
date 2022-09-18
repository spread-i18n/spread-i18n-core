package com.andro.spreadi18ncore.localization

import java.nio.file.Path

internal interface LocalizationFile {

    val path: Path
    val languageTag: LanguageTag
    val isDefault: Boolean
    fun containsTranslationIdentifiedBy(otherLanguageTag: LanguageTag): Boolean
}
internal data class AndroidLocalizationFile(override val path: Path):
    LocalizationFile {

    override val languageTag: LanguageTag by lazy {
        extractLanguageTagFromPath()
    }

    override val isDefault: Boolean by lazy {
        languageTag.isDefault
    }
    override fun containsTranslationIdentifiedBy(otherLanguageTag: LanguageTag): Boolean {
        return if (otherLanguageTag.isDefault && isDefault) {
            true
        } else languageTag == otherLanguageTag
    }

    private fun extractLanguageTagFromPath(): LanguageTag {
        with(path.toFile().name) {
            if (this == "values") {
                return LanguageTag.default
            } else if (startsWith("values-")) {
                return LanguageTag.extractFromString(removePrefix("values-"))
            }
            throw LanguageTagExtractionError(this)
        }
    }
}


@Suppress("ClassName")
internal data class iOSLocalizationFile(override val path: Path, override val isDefault: Boolean): LocalizationFile {

    override val languageTag: LanguageTag by lazy {
        extractLanguageTagFromPath()
    }
    override fun containsTranslationIdentifiedBy(otherLanguageTag: LanguageTag): Boolean {
        return if (otherLanguageTag.isDefault && isDefault) {
            true
        } else languageTag == otherLanguageTag
    }

    private fun extractLanguageTagFromPath(): LanguageTag {
        with(path.toFile().name) {
            if (endsWith(".lproj")) {
                val languageTag = removeSuffix(".lproj")
                return LanguageTag.extractFromString(languageTag)
            }
            throw LanguageTagExtractionError(this)
        }
    }
}
