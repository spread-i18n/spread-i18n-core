package com.andro.spreadi18ncore.sourcesheet

import com.andro.spreadi18ncore.importing.ImportException

internal enum class TranslationKeyType {
    iOS {
        override val cellText: List<String>
            get() = listOf("ios")
    },
    Android {
        override val cellText: List<String>
            get() = listOf("android")
    },
    General {
        override val cellText: List<String>
            get() = listOf("key", "identifier", "id")
    };
    abstract val cellText: List<String>
}

internal class ColumnNotFound(): ImportException("")

internal data class TranslationKeyColumn(val columnIndex: Int, val translationKeyType: TranslationKeyType)

internal class TranslationKeyColumns() {
    private val translationKeyColumns = mutableSetOf<TranslationKeyColumn>()
    fun add(translationKeyColumn: TranslationKeyColumn) {
        translationKeyColumns.add(translationKeyColumn)
    }
    fun isNotEmpty() = translationKeyColumns.isNotEmpty()

    private fun findTranslationKeyColumn(translationKeyType: TranslationKeyType): TranslationKeyColumn? {
        return translationKeyColumns.find { translationKeyType == it.translationKeyType }
    }

    fun containsTranslationKeyColumnFor(translationKeyType: TranslationKeyType): Boolean {
        return findTranslationKeyColumn(translationKeyType)!=null
    }

    fun getTranslationKeyColumn(translationKeyType: TranslationKeyType): TranslationKeyColumn {
        return findTranslationKeyColumn(translationKeyType) ?: throw ColumnNotFound()
    }
}
