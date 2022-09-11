package com.andro.spreadi18ncore.transfer.transformation

class CustomValueTransformation(private val transformationMap:Map<String, String>): ValueTransformation {
    constructor(vararg transformations: Pair<String, String>):this(transformations.toMap())
    override fun transform(value: String): String {
        return transformationMap.toList().fold(value){ acc, kv -> acc.replace(kv.first, kv.second) }
    }
}