package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.filewriting.iOSTranslationKeyValueWriter
import com.andro.spreadi18ncore.targetproject.LanguageTag
import java.io.File
import java.nio.file.Path

@Suppress("ClassName")
internal class iOSProjectStructure(private val projectPath: Path) {

    private val localisationFiles = mutableListOf<LocalizationFileContent>()

    fun withLocalizationFile(languageTagCandidate: String, block: LocalizationFileContent.() -> Unit): iOSProjectStructure {
        val file = LocalizationFileContent(LanguageTag.extractFromString(languageTagCandidate))
        file.block()
        localisationFiles.add(file)
        return this
    }

    fun create(): iOSProjectStructure {
        val projectDir = File(projectPath.toString())
        projectDir.mkdirs()

        Path.of(projectPath.toString(), "proj.xcodeproj").toFile().mkdirs()

        localisationFiles.forEach { fileContent ->
            val localeDirPath = Path.of(projectPath.toString(), "proj/${fileContent.languageTag.canonical}.lproj")
            localeDirPath.toFile().mkdirs()
            iOSTranslationKeyValueWriter(localeDirPath).use { writer ->
                fileContent.translations.forEach { keyValue ->
                    writer.write(keyValue)
                }
            }
        }
        return this
    }
}