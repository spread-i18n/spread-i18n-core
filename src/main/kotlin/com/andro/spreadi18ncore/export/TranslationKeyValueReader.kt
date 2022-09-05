package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.project.CommentIndicator
import com.andro.spreadi18ncore.project.NonTranslatableIndicator
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import java.io.Closeable

internal data class KeyValue(val key: String, val value: String)

val String.indicatesComment get() = startsWith(CommentIndicator)
val String.commentText get() = replace("^\\Q$CommentIndicator\\E *".toRegex(), "")//escape all between \\Q and \\E
val String.withCommentIndicator get() = if (indicatesComment) this else "$CommentIndicator$this"

val String.indicatesNonTranslatable get() = startsWith(NonTranslatableIndicator)
val String.translatable get() = replace("^\\Q$NonTranslatableIndicator\\E".toRegex(), "")//escape all between \\Q and \\E
val String.withNonTranslatableIndicator get() = if (indicatesNonTranslatable) this else "$NonTranslatableIndicator$this"
internal interface TranslationKeyValueReader: Closeable {
    fun read(valueTransformation: ValueTransformation? = null): KeyValue?
}