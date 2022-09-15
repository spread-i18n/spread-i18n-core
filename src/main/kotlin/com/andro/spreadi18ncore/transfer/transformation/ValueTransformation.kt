package com.andro.spreadi18ncore.transfer.transformation


typealias ValueTransformations = Map<String, String>
internal fun String.escape(valueTransformation: ValueTransformation?): String {
    return valueTransformation?.transform(this) ?: this
}
interface ValueTransformation {
    fun transform(value: String): String
}
