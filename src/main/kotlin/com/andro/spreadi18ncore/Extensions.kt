package com.andro.spreadi18ncore

internal fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)
internal fun <T, R> Sequence<T>.firstOrNullMapped(transform: (T) -> R?): R? {
    for (element in this) {
        transform(element)?.let {
            return it
        }
    }
    return null
}
