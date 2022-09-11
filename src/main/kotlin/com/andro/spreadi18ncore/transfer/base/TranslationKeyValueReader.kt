package com.andro.spreadi18ncore.transfer.base

import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import java.io.Closeable

internal interface TranslationKeyValueReader: Closeable {
    fun read(valueTransformation: ValueTransformation? = null): KeyValue?
}