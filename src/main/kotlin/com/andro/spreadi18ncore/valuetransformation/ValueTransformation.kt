package com.andro.spreadi18ncore.valuetransformation

interface ValueTransformation {
    fun transform(value: String): String
}

@Suppress("ClassName")
class iOSDefaultValueTransformation: ValueTransformation {
    override fun transform(value: String): String {
        return value
                .replace("%s", "%@")
                .replace("\"", "\\\"")
                .trim()
    }
}

class AndroidDefaultValueTransformation: ValueTransformation {
    override fun transform(value: String): String {
        return value
                .replace("\"", "\\\"")
                .replace("\'", "\\\'")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .trim()
    }
}

class CustomValueTransformation(private val transformationMap:Map<String, String>): ValueTransformation {
    override fun transform(value: String): String {
        return transformationMap.toList().fold(value){ acc, kv -> acc.replace(kv.first, kv.second) }
    }
}