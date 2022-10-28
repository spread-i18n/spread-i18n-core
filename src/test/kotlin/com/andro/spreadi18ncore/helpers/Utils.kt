package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.localization.LanguageTag
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

inline class Comment(val value: String)
internal class LocaleFile(private val makeReader: () -> TranslationKeyValueReader) {
    fun contains(wantedKeyValue: KeyValue): Boolean {
        makeReader().use { reader ->
            var keyValue = reader.read()
            while (keyValue != null) {
                if (keyValue == wantedKeyValue) {
                    return true
                }
                keyValue = reader.read()
            }
        }
        return false
    }

    fun contains(comment: Comment): Boolean {
        makeReader().use { reader ->
            var keyValue = reader.read()
            while (keyValue != null) {
                with(keyValue.key) {
                    if (startsWith("//")) {
                        if (substring(2).trimStart() == comment.value) {
                            return true
                        }
                    }
                }
                keyValue = reader.read()
            }
        }
        return false
    }
}


internal class RawLocaleFile(private val filePath: Path) {
    fun containsInLine(vararg tokens: String): Boolean {
        return allFilePaths.find { containsInLine(it, tokens.toList()) } != null
    }

    fun containsInLine(filePath: Path, tokens: List<String>): Boolean {
        makeReader(filePath).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                if (tokens.find { !line.contains(it) } == null) {
                    return true
                }
                line = reader.readLine()
            }
        }
        return false
    }
    private val allFilePaths: List<Path> by lazy {
        if (filePath.toFile().isDirectory) {
            filePath.toFile().listFiles { file -> !file.isDirectory }.map { Path.of(it.absolutePath) }
        } else {
            listOf(filePath)
        }
    }

    private fun makeReader(filePath: Path): BufferedReader {
        return Files.newBufferedReader(filePath)
    }

}

internal fun Project.localeFile(tagCandidate: String): LocaleFile {
    val languageTag = LanguageTag.fromString(tagCandidate)
    return localizationFiles.firstOrNull { it.containsTranslationIdentifiedBy(languageTag) }?.let {
        LocaleFile { keyValueReader(it) }
    } ?: throw IllegalArgumentException(tagCandidate)
}

internal fun Project.rawLocaleFile(tagCandidate: String): RawLocaleFile {
    val languageTag = LanguageTag.fromString(tagCandidate)
    return localizationFiles.firstOrNull { it.containsTranslationIdentifiedBy(languageTag) }?.let {
        RawLocaleFile(it.path)
    } ?: throw IllegalArgumentException(tagCandidate)
}