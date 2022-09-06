package com.andro.spreadi18ncore.transfer.base

import com.andro.spreadi18ncore.transfer.translation.TranslationTable

internal interface TranslationTableReader {
    fun read(): TranslationTable
}
