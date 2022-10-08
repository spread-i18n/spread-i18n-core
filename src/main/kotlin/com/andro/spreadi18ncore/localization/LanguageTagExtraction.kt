package com.andro.spreadi18ncore.localization

import java.io.File

internal interface LanguageTagExtractor {
    fun extract(file: File): LanguageTag?
}

internal object AndroidTagExtractor : LanguageTagExtractor {
    override fun extract(file: File): LanguageTag? {
        val fileName = file.name
        return if (fileName == "values") {
            LanguageTag.default
        } else if (fileName.startsWith("values-")) {
            LanguageTag.extractFromString(fileName.removePrefix("values-"))
        } else {
            null
        }
    }
}

@Suppress("ClassName")
internal object iOSTagExtractor : LanguageTagExtractor {
    override fun extract(file: File): LanguageTag? {
        val fileName = file.name
        return if (fileName.endsWith(".lproj")) {
            LanguageTag.extractFromString(fileName.removeSuffix(".lproj"))
        } else {
            null
        }
    }
}

internal val TagExtractors = listOf(AndroidTagExtractor, iOSTagExtractor)