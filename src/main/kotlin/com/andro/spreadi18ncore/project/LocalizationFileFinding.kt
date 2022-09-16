package com.andro.spreadi18ncore.project

import com.andro.spreadi18ncore.mappedFirstOrNull
import com.andro.spreadi18ncore.localization.*
import com.andro.spreadi18ncore.localization.LocalizationFile
import com.andro.spreadi18ncore.localization.iOSLocalizationFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

val File.dirs: Array<File>
    get() = this.listFiles { file -> file.isDirectory } as Array<File>

val File.files: Array<File>
    get() = this.listFiles { file -> file.isFile } as Array<File>


internal interface LocalizationFileFinder {
    fun findLocalizationFilesIn(rootFile: File): List<LocalizationFile>
}

fun allDirsRecursively(parentFile: File): List<File> {
    return parentFile.dirs
        .map { file ->
            listOf(file) + allDirsRecursively(file)
        }
        .flatten()
}

@Suppress("ClassName")
internal object iOSLocalizationPathFinder {
    fun findLocalizationsDirIn(rootFile: File): List<Path> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.endsWith(".lproj") && (dir.name != "Base.lproj") }
            .map {
                it.toPath()
            }
    }
}
    @Suppress("ClassName")
internal object iOSLocalizationFileFinder : LocalizationFileFinder {
    override fun findLocalizationFilesIn(rootFile: File): List<LocalizationFile> {
        val developmentLanguage = extractDevelopmentLanguageFromProject(rootFile) ?: LanguageTag.english
        return iOSLocalizationPathFinder.findLocalizationsDirIn(rootFile).map {
            val languageTag = LanguageTag.extractFromPath(it)
            iOSLocalizationFile(it, developmentLanguage == languageTag)
        }
    }

    private fun extractDevelopmentLanguageFromProject(rootFile: File): LanguageTag? {
        return pbxprojFile.findPathIn(rootFile.toPath())?.let { pbxprojFilePath ->
            return Files.newBufferedReader(pbxprojFilePath).use { reader ->
                reader.lineSequence().mappedFirstOrNull { line ->
                    iOSDevelopmentLanguageExtractor.extract(line)
                }
            }
        }
    }
}

@Suppress("ClassName")
internal object iOSDevelopmentLanguageExtractor {

    private val regex = Regex("""\s*developmentRegion\s*=\s*([a-z-A-Z]+);?""")
    fun extract(developmentLanguageLineCandidate: String): LanguageTag? {
        return if (developmentLanguageLineCandidate.contains("developmentRegion")) {
            regex.matchEntire(developmentLanguageLineCandidate)?.groups?.filterNotNull()?.let { group ->
                if (group.size == 2) {
                    LanguageTag.extractFromStringOrNull(group[1].value)
                } else null
            }
        } else {
            null
        }
    }
}

internal object AndroidLocalizationFileFinder :
    LocalizationFileFinder {
    override fun findLocalizationFilesIn(rootFile: File): List<LocalizationFile> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.startsWith("values") && dir.files.any { it.name == "strings.xml" } }
            .map { AndroidLocalizationFile(it.toPath()) }
    }
}
