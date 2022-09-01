package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.targetproject.CommentIndicator
import com.andro.spreadi18ncore.targetproject.NonTranslatableIndicator
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import java.io.Closeable

internal data class KeyValue(val key: String, val value: String)

val String.indicatesComment get() = startsWith(CommentIndicator)
val String.commentText get() = replace("^\\Q$CommentIndicator\\E *".toRegex(), "")//escape all between \\Q and \\E

val String.indicatesNonTranslatable get() = startsWith(NonTranslatableIndicator)
val String.translatable get() = replace("^\\Q$NonTranslatableIndicator\\E".toRegex(), "")//escape all between \\Q and \\E

internal interface TranslationKeyValueReader: Closeable {
    fun read(valueTransformation: ValueTransformation? = null): KeyValue?
}