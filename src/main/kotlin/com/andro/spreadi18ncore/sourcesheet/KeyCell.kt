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

internal data class KeyCell(val rowIndex: RowIndex, val columnIndex: ColumnIndex, val keyType: TranslationKeyType)

internal class KeyCells() {
    private val keyCells = mutableSetOf<KeyCell>()
    fun add(keyCells: KeyCell) {
        this.keyCells.add(keyCells)
    }
    fun isNotEmpty() = keyCells.isNotEmpty()

    private fun findKeyCell(translationKeyType: TranslationKeyType): KeyCell? {
        return keyCells.find { translationKeyType == it.keyType }
    }

    fun containsKeyCellFor(translationKeyType: TranslationKeyType): Boolean {
        return findKeyCell(translationKeyType)!=null
    }

    fun getKeyCell(translationKeyType: TranslationKeyType): KeyCell {
        return findKeyCell(translationKeyType) ?: throw ColumnNotFound()
    }
}
