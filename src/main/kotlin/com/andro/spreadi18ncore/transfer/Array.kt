package com.andro.spreadi18ncore.transfer

const val ArrayIndicator = "-array"
val String.indicatesArray get() = endsWith(ArrayIndicator)

val String.withoutArrayIndicator get() = replace(ArrayIndicator, "")
val String.withArrayIndicator get() = if (indicatesArray) this else "$this$ArrayIndicator"
