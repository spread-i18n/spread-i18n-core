package com.andro.spreadi18ncore.filewriting

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.sourcesheet.ImportException
import java.io.Closeable
import java.io.IOException

internal class CanNotAccessLocalizationFile(exc: IOException) : ImportException(cause = exc)

internal interface TranslationKeyValueWriter: Closeable {
    fun write(keyValue: KeyValue)
}

