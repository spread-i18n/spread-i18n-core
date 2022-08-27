package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.targetproject.LanguageTag

internal class LocalizationFileContent(val languageTag: String) {

    private val _translations = mutableListOf<KeyValue>()
    val translations: List<KeyValue> = _translations

    fun translation(translation: KeyValue) {
        _translations.add(translation)
    }

    fun withTranslations(block: () -> List<KeyValue>) {
        _translations.addAll(block())
    }
}