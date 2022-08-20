package com.andro.spreadi18ncore.export

import java.io.Closeable

internal data class KeyValue(val key: String, val value: String)

internal interface TranslationKeyValueReader: Closeable {
    fun read(): KeyValue?
}