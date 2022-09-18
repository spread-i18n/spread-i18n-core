package com.andro.spreadi18ncore.helpers

import com.andro.spreadi18ncore.transfer.importing.iOSTranslationKeyValueWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Suppress("ClassName")
internal class iOSProjectStructure(private val projectPath: Path, private val developmentLanguage: String) {

    private val localisationFiles = mutableListOf<LocalizationFileContent>()

    fun withLocalizationFile(tagCandidate: String, block: LocalizationFileContent.() -> Unit): iOSProjectStructure {
        val file = LocalizationFileContent(tagCandidate)
        file.block()
        localisationFiles.add(file)
        return this
    }

    fun create(): iOSProjectStructure {
        val projectDir = File(projectPath.toString())
        projectDir.mkdirs()

        val xcodeProjectPath = Path.of(projectPath.toString(), "proj.xcodeproj")
        xcodeProjectPath.toFile().mkdirs()

        val pbxprojFilePath = Path.of(xcodeProjectPath.toString(),"project.pbxproj")
        Files.newBufferedWriter(pbxprojFilePath).use { writer ->
            writer.write("\tdevelopmentRegion = $developmentLanguage")
        }

        localisationFiles.forEach { fileContent ->
            val directoryName = with(fileContent.languageTag) {
                if (this == "default") developmentLanguage else this
            }
            val localeDirPath = Path.of(projectPath.toString(), "proj/$directoryName.lproj")
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