package com.andro.spreadi18ncore.excel

import com.andro.spreadi18ncore.transfer.TransferException

internal class KeyCellNotFound(keyType: KeyType)
    : TransferException(message = "Key cell for: ${keyType.name} not found")

internal data class KeyCell(val rowIndex: RowIndex, val columnIndex: ColumnIndex, val keyType: KeyType)

internal class KeyCells {
    private val keyCells = mutableSetOf<KeyCell>()
    fun add(keyCells: KeyCell) {
        this.keyCells.add(keyCells)
    }

    fun isNotEmpty() = keyCells.isNotEmpty()

    private fun findKeyCell(keyType: KeyType): KeyCell? {
        return keyCells.find { keyType == it.keyType }
    }

    fun containsKeyCellFor(keyType: KeyType): Boolean {
        return findKeyCell(keyType) != null
    }

    fun getKeyCell(keyType: KeyType): KeyCell {
        return findKeyCell(keyType) ?: throw KeyCellNotFound(keyType)
    }
}
