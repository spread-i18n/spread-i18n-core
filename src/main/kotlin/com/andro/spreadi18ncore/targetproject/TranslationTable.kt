package com.andro.spreadi18ncore.targetproject

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.export.KeyValue

const val NonTranslatableIndicator = "*"
const val CommentIndicator = "//"
internal class TranslationRow(val key: String, valueCount: Int) {
    private val _values = MutableList(size = valueCount, init = { "" })
    val values: List<String> = _values

    fun set(at: Int, value: String) {
        _values[at] = value
    }
}

data class LocaleValue(val value: String) {
    val friendlyValue: String get() = value.ifEmpty { "English" }
}

internal class TranslationTable(val languageTags: List<LanguageTag>) {

    private val _translationRows = mutableListOf<TranslationRow>()
    val translationRows: List<TranslationRow> = _translationRows

    fun setValue(languageTag: LanguageTag, keyValue: KeyValue) {
        loadTranslationRow(keyValue.key).set(languageTags.indexOf(languageTag), keyValue.value)
    }

    private fun loadTranslationRow(key: String): TranslationRow {
        return _translationRows.firstOrNull { it.key == key } ?: run {
            val translation = TranslationRow(key, languageTags.size)
            _translationRows.add(translation)
            translation
        }
    }

    fun keyValues(languageTag: LanguageTag): List<KeyValue> {
        return languageTags.withIndex().firstOrNull { it.value == languageTag }?.let { indexedValue ->
            translationRows.map { row -> KeyValue(row.key, row.values[indexedValue.index]) }
        } ?: emptyList()
    }
}

internal interface TranslationTableReader {
    fun read(): TranslationTable
}

internal interface TranslationTableWriter {
    fun write(translationTable: TranslationTable)
}

internal val Project.languageTags: List<LanguageTag> get() = localizationFiles.map { it.languageTag }