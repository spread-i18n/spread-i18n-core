package com.andro.spreadi18ncore.transfer.transformation

@Suppress("ClassName")
object iOSEscaping {

    private val escapingMap = mapOf(
        "\"" to "\\\"",
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
