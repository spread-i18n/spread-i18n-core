package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.importing.ValueTransformations
import com.andro.spreadi18ncore.targetproject.LocalizationDirectory
import org.apache.poi.ss.usermodel.Sheet

internal class Translations(val key: String, valueCount: Int) {
    private val _values = MutableList(size = valueCount, init = { "" })
    val values: List<String> = _values

    fun set(at: Int, value: String) {
        _values[at] = value
    }
}

inline class LocaleValue(private val locale: String) {
    val friendlyValue: String get() = if (locale.isNullOrEmpty()) { "Base" } else locale
}

internal class TranslationTable(val localeValues: List<LocaleValue>) {

    private val _translations = mutableListOf<Translations>()
    val translations: List<Translations> = _translations

    fun setValue(localeValue: LocaleValue, keyValue: KeyValue) {
        loadTranslation(keyValue.key).set(localeValues.indexOf(localeValue), keyValue.value)
    }

    private fun loadTranslation(key: String): Translations {
        return _translations.firstOrNull { it.key == key } ?: run {
            val translation = Translations(key, localeValues.size)
            _translations.add(translation)
            translation
        }
    }
}

internal interface TranslationTableReader {
    fun read(): TranslationTable
}



internal fun Project.extractLocaleValue(localizationDirectory: LocalizationDirectory): LocaleValue =
    type.localeValueExtractor.extract(localizationDirectory)

internal interface TranslationTableWriter {
    fun write(translationTable: TranslationTable)
}

internal val Project.locales: List<LocaleValue>
    get() {
        return localizationDirectories.map { type.localeValueExtractor.extract(it) }
    }