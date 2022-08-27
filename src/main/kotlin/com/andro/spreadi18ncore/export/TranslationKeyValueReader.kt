package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import java.io.Closeable

internal data class KeyValue(val key: String, val value: String)

internal interface TranslationKeyValueReader: Closeable {
    fun read(valueTransformation: ValueTransformation? = null): KeyValue?
}