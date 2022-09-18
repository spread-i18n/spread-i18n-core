package com.andro.spreadi18ncore.excel

import com.andro.spreadi18ncore.transfer.TransferException

internal class ColumnNotFound : TransferException("")

internal data class KeyCell(val rowIndex: RowIndex, val columnIndex: ColumnIndex, val keyType: TranslationKeyType)

internal class KeyCells {
    private val keyCells = mutableSetOf<KeyCell>()
    fun add(keyCells: KeyCell) {
        this.keyCells.add(keyCells)
    }

    fun isNotEmpty() = keyCells.isNotEmpty()

    private fun findKeyCell(translationKeyType: TranslationKeyType): KeyCell? {
        return keyCells.find { translationKeyType == it.keyType }
    }

    fun containsKeyCellFor(translationKeyType: TranslationKeyType): Boolean {
        return findKeyCell(translationKeyType) != null
    }

    fun getKeyCell(translationKeyType: TranslationKeyType): KeyCell {
        return findKeyCell(translationKeyType) ?: throw ColumnNotFound()
    }
}
