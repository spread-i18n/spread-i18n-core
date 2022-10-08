package com.andro.spreadi18ncore.localization

import java.nio.file.Path

internal interface LocalizationFile {

    val path: Path
    val languageTag: LanguageTag
    val isDefault: Boolean
    fun containsTranslationIdentifiedBy(otherLanguageTag: LanguageTag): Boolean
}

internal data class AndroidLocalizationFile(override val path: Path) :
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
        return with(path.toFile()) {
            AndroidTagExtractor.extract(this) ?: throw LanguageTagExtractionError(this.name)
        }
    }
}


@Suppress("ClassName")
internal data class iOSLocalizationFile(override val path: Path, override val isDefault: Boolean) : LocalizationFile {

    override val languageTag: LanguageTag by lazy {
        extractLanguageTagFromPath()
    }

    override fun containsTranslationIdentifiedBy(otherLanguageTag: LanguageTag): Boolean {
        return if (otherLanguageTag.isDefault && isDefault) {
            true
        } else languageTag == otherLanguageTag
    }

    private fun extractLanguageTagFromPath(): LanguageTag {
        return with(path.toFile()) {
            iOSTagExtractor.extract(this) ?: throw LanguageTagExtractionError(this.name)
        }
    }
}
