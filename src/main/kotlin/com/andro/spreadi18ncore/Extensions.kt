package com.andro.spreadi18ncore

internal fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)
internal fun <T, R> Sequence<T>.mappedFirstOrNull(transform: (T) -> R?): R? {
    return mapNotNull { transform(it) }.firstOrNull()
}

internal val String.withNewLineIfNotBlank get() = if (isNotBlank()) "$this\n" else this
