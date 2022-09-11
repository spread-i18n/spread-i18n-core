package com.andro.spreadi18ncore.transfer.transformation

internal fun String.transform(valueTransformation: ValueTransformation?): String {
    return valueTransformation?.transform(this) ?: this
}
interface ValueTransformation {
    fun transform(value: String): String
}
