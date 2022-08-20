package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.filewriting.AndroidTranslationKeyValueWriter
import com.andro.spreadi18ncore.targetproject.LanguageTag
import java.io.File
import java.nio.file.Path


internal class AndroidProjectStructure(private val projectPath: Path) {

    private val localizationFiles = mutableListOf<LocalizationFileContent>()

    fun withLocalizationFile(languageTagCandidate: String, block: LocalizationFileContent.() -> Unit): AndroidProjectStructure {
        val file = LocalizationFileContent(LanguageTag.extractFromString(languageTagCandidate))
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
            val localeDirPath = Path.of(projectPath.toString(), "src/main/res/values-${fileContent.languageTag.canonical}")
            localeDirPath.toFile().mkdirs()
            AndroidTranslationKeyValueWriter(localeDirPath).use { fileWriter ->
                fileContent.translations.forEach { keyValue ->
                    fileWriter.write(keyValue)
                }
            }
        }
    }
}