package com.andro.spreadi18ncore.sourcesheet

import com.andro.spreadi18ncore.sourcetargetmatching.Locales
import com.andro.spreadi18ncore.sourcetargetmatching.identifiedBy
import java.util.*

inline class RowIndex(val value: Int)
inline class ColumnIndex(val value: Int)

internal data class LocaleCell(val rowIndex: RowIndex, val columnIndex: ColumnIndex, val text: String) {//source point
    val locales: List<Locale> by lazy {
        Locales.allLocales.items.filter { locale -> locale.identifiedBy(text) }
    }
}

internal data class LocaleCells(private val cells: MutableSet<LocaleCell> = mutableSetOf())
    :Collection<LocaleCell> by cells{
    fun add(localeCell: LocaleCell) {
        cells.add(localeCell)
    }
    fun isNotEmpty() = cells.isNotEmpty()
}