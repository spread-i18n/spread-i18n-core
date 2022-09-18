package com.andro.spreadi18ncore.transfer.transformation

import com.andro.spreadi18ncore.transfer.rename


typealias ValueTransformations = Map<String, String>
internal fun String.transformed(by: ValueTransformation?): String {
    return rename(from = by, to = { valueTransformation ->
        valueTransformation?.transform(this) ?: this
    })
}
interface ValueTransformation {
    fun transform(value: String): String
}
