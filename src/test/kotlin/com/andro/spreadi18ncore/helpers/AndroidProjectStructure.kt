package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.filewriting.AndroidTranslationFileWriter
import java.io.File
import java.nio.file.Path
import java.util.*


internal class AndroidProjectStructure(private val projectPath: Path) {

    private val localisationFiles = mutableListOf<LocalizationFileContent>()

    fun localizationFile(locale: Locale, block: LocalizationFileContent.() -> Unit): AndroidProjectStructure {
        val file = LocalizationFileContent(locale)
        file.block()
        localisationFiles.add(file)
        return this
    }

    fun create() {
        val projectDir = File(projectPath.toString())
        projectDir.mkdirs()

        Path.of(projectPath.toString(), "src/main/res/").toFile().mkdirs()
        Path.of(projectPath.toString(), "src/main/AndroidManifest.xml").toFile().createNewFile()

        localisationFiles.forEach { fileContent ->
            val localeDirPath = Path.of(projectPath.toString(), "src/main/res/values-${fileContent.locale.language}")
            localeDirPath.toFile().mkdirs()
            AndroidTranslationFileWriter(localeDirPath).use { fileWriter ->
                fileContent.translations.forEach { keyValue ->
                    fileWriter.write(keyValue.key, keyValue.value)
                }
            }
        }
    }
}