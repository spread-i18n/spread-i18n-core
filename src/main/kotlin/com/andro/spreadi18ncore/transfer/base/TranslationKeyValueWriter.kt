package com.andro.spreadi18ncore.transfer.base

import com.andro.spreadi18ncore.transfer.TransferException
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import java.io.Closeable
import java.io.IOException

internal class CanNotAccessLocalizationFile(exc: IOException)
    : TransferException(message = "Could not access a localization file: ${exc.message}", cause = exc)

internal interface TranslationKeyValueWriter: Closeable {
    fun write(keyValue: KeyValue)
}

