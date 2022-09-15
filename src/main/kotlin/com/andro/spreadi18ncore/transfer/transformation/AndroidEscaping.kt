package com.andro.spreadi18ncore.transfer.transformation

import java.util.AbstractMap
internal fun <K, V> Map.Entry<K, V>.swapKeyWithValue(): Map.Entry<V, K> {
    return AbstractMap.SimpleEntry(value, key)
}

//https://developer.android.com/guide/topics/resources/string-resource#escaping_quotes
internal object AndroidEscaping {

    private val escapingMap = mapOf(
            "\"" to "\\\"",
            "\'" to "\\\'",
            "&" to "&amp;",
            "\n" to "\\n",
            "\t" to "\\t",
            "@" to "\\@",
            "?" to "\\?",
        //"<" to "&lt;",
            //">" to "&gt;"
    )

    private val unescapingEntries: List<Map.Entry<String, String>> by lazy {
        escapingMap.map { it.swapKeyWithValue() }
    }

    fun escape(value: String): String {
        return escapingMap.entries.escape(value)
    }

    fun unescape(value: String): String {
        return unescapingEntries.escape(value)
    }

    private fun Collection<Map.Entry<String, String>>.escape(value: String): String {
        return fold(value) { escapedValue, (oldValue, newValue) -> escapedValue.replace(oldValue, newValue) }
    }
}