package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.ImportException
import java.io.Closeable
import java.io.IOException

internal class CanNotAccessLocalizationFile(exc: IOException) : ImportException(cause = exc)

internal interface TranslationFileWriter: Closeable {
    fun write(key: String, value: String)
}

