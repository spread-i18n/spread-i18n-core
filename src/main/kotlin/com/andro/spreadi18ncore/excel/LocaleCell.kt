package com.andro.spreadi18ncore.excel

import com.andro.spreadi18ncore.localization.LanguageTag


inline class RowIndex(val value: Int)
inline class ColumnIndex(val value: Int)

internal data class LocaleCell(
    val rowIndex: RowIndex,
    val columnIndex: ColumnIndex,
    val languageTag: LanguageTag
) {//source point
}

internal data class LocaleCells(private val cells: MutableSet<LocaleCell> = mutableSetOf()) :
    Collection<LocaleCell> by cells {
    fun add(localeCell: LocaleCell) {
        cells.add(localeCell)
    }

    fun isNotEmpty() = cells.isNotEmpty()
}