package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.targetproject.LocalizationDirectory
import java.io.BufferedReader
import java.nio.file.Files

@Suppress("ClassName")
internal class iOSTranslationFileReader(private val localizationDirectory: LocalizationDirectory) : TranslationFileReader {

    private var currentReader: BufferedReader?
    private var availableReaders = mutableListOf<BufferedReader>()
    init {
        val localizableFilePath = localizationDirectory.path.resolve("Localizable.strings")
        if (Files.exists(localizableFilePath)) {
            availableReaders.add(Files.newBufferedReader(localizableFilePath))
        }
        val infoPlistFilePath = localizationDirectory.path.resolve("InfoPlist.strings")
        if (Files.exists(infoPlistFilePath)) {
            availableReaders.add(Files.newBufferedReader(infoPlistFilePath))
        }
        currentReader = availableReaders.firstOrNull()
    }

    override fun read(): KeyValue? {
        currentReader?.let {
            val keyValue = it.readKeyValue()
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
            val indexOfNextReader = index+1
            if (indexOfNextReader < availableReaders.size) {
                availableReaders[indexOfNextReader]
            } else null
        } else null
    }

    private val iOSKeyValueRegex = Regex("""^"(.*)".*=.*"(.*)";.*""")
    private fun extractKeyValue(from: String): KeyValue? {
        return iOSKeyValueRegex.matchEntire(from)?.groups?.filterNotNull()?.let { groups ->
            if (groups.size == 3) {
                KeyValue(groups[1].value, groups[2].value)
            } else null
        }
    }

    private fun BufferedReader.readKeyValue(): KeyValue? {
        var line = readLine()
        while (line != null) {
            val keyValue = extractKeyValue(from = line)
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