package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.filewriting.AndroidTranslationFileWriter
import com.andro.spreadi18ncore.filewriting.iOSTranslationFileWriter
import java.io.File
import java.nio.file.Path
import java.util.*

@Suppress("ClassName")
internal class iOSProjectStructure(private val projectPath: Path) {

    private val localisationFiles = mutableListOf<LocalizationFileContent>()

    fun localizationFile(locale: Locale, block: LocalizationFileContent.() -> Unit): iOSProjectStructure {
        val file = LocalizationFileContent(locale)
        file.block()
        localisationFiles.add(file)
        return this
    }

    fun create() {
        val projectDir = File(projectPath.toString())
        projectDir.mkdirs()

        Path.of(projectPath.toString(), "proj.xcodeproj").toFile().mkdirs()

        localisationFiles.forEach { fileContent ->
            val localeDirPath = Path.of(projectPath.toString(), "proj/${fileContent.locale.language}.lproj")
            localeDirPath.toFile().mkdirs()
            iOSTranslationFileWriter(localeDirPath).use { fileWriter ->
                fileContent.translations.forEach { keyValue ->
                    fileWriter.write(keyValue.key, keyValue.value)
                }
            }
        }
    }
}