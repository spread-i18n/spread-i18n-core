package com.andro.spreadi18ncore

internal fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)
