package com.andro.spreadi18ncore.transfer.base

import com.andro.spreadi18ncore.transfer.translation.TranslationTable

internal interface TranslationTableWriter {
    fun write(translationTable: TranslationTable)
}
