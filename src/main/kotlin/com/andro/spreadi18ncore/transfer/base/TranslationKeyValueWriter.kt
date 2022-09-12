package com.andro.spreadi18ncore.transfer.base

import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.excel.TransferException
import java.io.Closeable
import java.io.IOException

internal class CanNotAccessLocalizationFile(exc: IOException) : TransferException(cause = exc)

internal interface TranslationKeyValueWriter: Closeable {
    fun write(keyValue: KeyValue)
}

