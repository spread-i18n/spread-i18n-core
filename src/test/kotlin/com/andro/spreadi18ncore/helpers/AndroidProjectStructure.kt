package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.transfer.importing.AndroidTranslationKeyValueWriter
import java.io.File
import java.nio.file.Path


internal class AndroidProjectStructure(private val projectPath: Path) {

    private val localizationFiles = mutableListOf<LocalizationFileContent>()

    fun withLocalizationFile(languageTag: String, block: LocalizationFileContent.() -> Unit): AndroidProjectStructure {
        val file = LocalizationFileContent(languageTag)
        file.block()
        localizationFiles.add(file)
        return this
    }

    fun create() {
        val projectDir = File(projectPath.toString())
        projectDir.mkdirs()

        Path.of(projectPath.toString(), "src/main/res/").toFile().mkdirs()
        Path.of(projectPath.toString(), "src/main/AndroidManifest.xml").toFile().createNewFile()

        localizationFiles.forEach { fileContent ->
            val directoryName = with(fileContent.languageTag) {
                if(isEmpty()) "values"
                else "values-$this"
            }
            val localeDirPath = Path.of(projectPath.toString(), "src/main/res/$directoryName")
            localeDirPath.toFile().mkdirs()
            AndroidTranslationKeyValueWriter(localeDirPath).use { fileWriter ->
                fileContent.translations.forEach { keyValue ->
                    fileWriter.write(keyValue)
                }
            }
        }
    }
}