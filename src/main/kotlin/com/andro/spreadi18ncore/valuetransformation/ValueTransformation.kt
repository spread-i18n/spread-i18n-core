package com.andro.spreadi18ncore.valuetransformation

internal fun String.transform(valueTransformation: ValueTransformation?): String {
    return valueTransformation?.transform(this) ?: this
}
interface ValueTransformation {
    fun transform(value: String): String
}



class CustomValueTransformation(private val transformationMap:Map<String, String>): ValueTransformation {
    constructor(vararg transformations: Pair<String, String>):this(emptyMap())
    override fun transform(value: String): String {
        return transformationMap.toList().fold(value){ acc, kv -> acc.replace(kv.first, kv.second) }
    }
}