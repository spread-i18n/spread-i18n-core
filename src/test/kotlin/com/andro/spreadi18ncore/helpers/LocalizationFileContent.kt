package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.transfer.translation.KeyValue

internal class LocalizationFileContent(val languageTag: String) {

    private val _translations = mutableListOf<KeyValue>()
    val translations: List<KeyValue> = _translations

    fun translation(translation: KeyValue) {
        _translations.add(translation)
    }

    fun translations(block: () -> List<KeyValue>) {
        _translations.addAll(block())
    }
}