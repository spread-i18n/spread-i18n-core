package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.indicatesComment
import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.escape
import com.andro.spreadi18ncore.transfer.transformation.iOSEscaping
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

@Suppress("ClassName")
internal class iOSTranslationKeyValueReader(pathOfLocalizationFile: Path) : TranslationKeyValueReader {

    private var currentReader: BufferedReader?
    private var availableReaders = mutableListOf<BufferedReader>()

    init {
        val localizableFilePath = pathOfLocalizationFile.resolve("Localizable.strings")
        if (Files.exists(localizableFilePath)) {
            availableReaders.add(Files.newBufferedReader(localizableFilePath))
        }
        val infoPlistFilePath = pathOfLocalizationFile.resolve("InfoPlist.strings")
        if (Files.exists(infoPlistFilePath)) {
            availableReaders.add(Files.newBufferedReader(infoPlistFilePath))
        }
        currentReader = availableReaders.firstOrNull()
    }

    override fun read(valueTransformation: ValueTransformation?): KeyValue? {
        currentReader?.let {
            val keyValue = it.readKeyValue(valueTransformation)
            if (keyValue != null) {
                return keyValue
            } else {
                currentReader = nextReader()
                return read()
            }
        }
        return null
    }

    private fun nextReader(): BufferedReader? {
        val index = availableReaders.indexOf(currentReader)
        return if (index >= 0) {
            val indexOfNextReader = index + 1
            if (indexOfNextReader < availableReaders.size) {
                availableReaders[indexOfNextReader]
            } else null
        } else null
    }

    private val iOSKeyValueRegex = Regex("""^"(.*)".*=.*"(.*)";.*""")
    private fun extractKeyValue(from: String, valueTransformation: ValueTransformation?): KeyValue? {
        if (from.indicatesComment) {
            return KeyValue(from, "")
        }
        return iOSKeyValueRegex.matchEntire(from)?.groups?.filterNotNull()?.let { groups ->
            if (groups.size == 3) {
                makeKeyValue(groups[1].value, groups[2].value, valueTransformation)
            } else null
        }
    }

    private fun makeKeyValue(key: String, value: String, valueTransformation: ValueTransformation?): KeyValue {
        return KeyValue(key, value.unescaped.escape(valueTransformation))
    }

    private val String.unescaped: String get() = iOSEscaping.unescape(this)

    private fun BufferedReader.readKeyValue(valueTransformation: ValueTransformation?): KeyValue? {
        var line = readLine()
        while (line != null) {
            val keyValue = extractKeyValue(from = line, valueTransformation)
            if (keyValue != null) {
                return keyValue
            } else {
                line = readLine()
            }
        }
        return null
    }

    override fun close() {
        availableReaders.forEach { it.close() }
    }
}