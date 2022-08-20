package com.andro.spreadi18ncore.targetproject

import java.io.File

val File.dirs: Array<File>
    get() = this.listFiles { file -> file.isDirectory }

val File.files: Array<File>
    get() = this.listFiles { file -> file.isFile }


internal interface LocalizationFileFinder {
    fun findLocalizationFileIn(rootFile: File): List<LocalizationFile>
}

fun allDirsRecursively(parentFile: File): List<File> {
    return parentFile.dirs
        .map { file ->
            listOf(file) + allDirsRecursively(file)
        }
        .flatten()
}

@Suppress("ClassName")
internal class iOSLocalizationFileFinder :
    LocalizationFileFinder {
    override fun findLocalizationFileIn(rootFile: File): List<LocalizationFile> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.endsWith(".lproj") }
            .map {
                LocalizationFile(it.toPath(), languageTag(it.name))
            }
    }

    private fun languageTag(localizationDirectoryName: String): LanguageTag {
        with(localizationDirectoryName) {
            if (this == "Base.lproj") {
                return LanguageTag.extractFromString("en")
            } else if (endsWith(".lproj")) {
                return LanguageTag.extractFromString(removeSuffix(".lproj"))
            }
            throw LanguageTagExtractionError(localizationDirectoryName)
        }
    }
}

internal class AndroidLocalizationFileFinder :
    LocalizationFileFinder {
    override fun findLocalizationFileIn(rootFile: File): List<LocalizationFile> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.startsWith("values") && dir.files.any { it.name == "strings.xml" } }
            .map { LocalizationFile(it.toPath(), languageTag(it.name)) }
    }

    private fun languageTag(localizationDirectoryName: String): LanguageTag {
        with(localizationDirectoryName) {
            if (this == "values") {
                return LanguageTag.extractFromString("en")
            } else if (startsWith("values-")) {
                return LanguageTag.extractFromString(removePrefix("values-"))
            }
            throw LanguageTagExtractionError(localizationDirectoryName)
        }
    }
}
