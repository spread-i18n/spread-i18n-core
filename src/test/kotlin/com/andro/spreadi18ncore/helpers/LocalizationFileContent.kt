package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.export.KeyValue
import java.util.*

internal class LocalizationFileContent(val locale: Locale = Locale.ENGLISH) {

    private val _translations = mutableListOf<KeyValue>()
    val translations: List<KeyValue> = _translations

    fun translation(translation: KeyValue) {
        _translations.add(translation)
    }

    fun translations(block: () -> List<KeyValue>) {
        _translations.addAll(block())
    }
}