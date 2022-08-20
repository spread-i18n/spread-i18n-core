package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.export.TranslationKeyValueReader
import com.andro.spreadi18ncore.targetproject.LanguageTag

internal class LocaleFile(private val makeReader: () -> TranslationKeyValueReader) {
    fun contains(wantedKeyValue: KeyValue): Boolean {
        makeReader().use { reader ->
            var keyValue = reader.read()
            while (keyValue != null) {
                if (keyValue == wantedKeyValue) {
                    return true
                }
                keyValue = reader.read()
            }
        }
        return false
    }
}

internal fun Project.localeFile(tagCandidate: String): LocaleFile {
    val languageTag = LanguageTag.extractFromString(tagCandidate)
    return localizationFiles.firstOrNull { it.languageTag == languageTag }?.let {
        LocaleFile { keyValueReader(it) }
    } ?: throw IllegalArgumentException(tagCandidate)
}