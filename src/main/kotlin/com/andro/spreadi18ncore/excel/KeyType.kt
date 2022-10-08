package com.andro.spreadi18ncore.excel

internal enum class KeyType {
    @Suppress("EnumEntryName")
    iOS {
        override val cellText: List<String>
            get() = listOf("ios")
    },
    Android {
        override val cellText: List<String>
            get() = listOf("android")
    },
    General {
        override val cellText: List<String>
            get() = listOf("key", "identifier", "id")
    };

    abstract val cellText: List<String>
}