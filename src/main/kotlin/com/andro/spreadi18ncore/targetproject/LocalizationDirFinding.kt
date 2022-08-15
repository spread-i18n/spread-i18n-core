package com.andro.spreadi18ncore.targetproject

import java.io.File
import java.nio.file.Path

val File.dirs: Array<File>
    get() = this.listFiles { file -> file.isDirectory }

val File.files: Array<File>
    get() = this.listFiles { file -> file.isFile }


internal interface LocalizationResourceFinder {
    fun findLocalizationDirectoriesIn(rootFile: File): List<LocalizationDirectory>
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
internal class iOSLocalizationDirectoriesFinder:
    LocalizationResourceFinder {
    override fun findLocalizationDirectoriesIn(rootFile: File): List<LocalizationDirectory> {
        return allDirsRecursively(rootFile)
                .filter { dir -> dir.name.endsWith(".lproj") }
                .map { LocalizationDirectory(it.toPath()) }
    }

    override fun findLocalizationFilesIn(rootFile: File): List<LocalizationFile> {
        return emptyList()
    }
}

internal class AndroidLocalizationResourceFinder:
    LocalizationResourceFinder {
    override fun findLocalizationDirectoriesIn(rootFile: File): List<LocalizationDirectory> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.startsWith("values") && dir.files.any { it.name == "strings.xml"} }
            .map { LocalizationDirectory(it.toPath()) }
    }

    override fun findLocalizationFilesIn(rootFile: File): List<LocalizationFile> {
        return allDirsRecursively(rootFile)
            .filter { dir -> dir.name.startsWith("values") && dir.files.any { it.name == "strings.xml"} }
            .map {
                LocalizationFile(Path.of(it.toPath().toString(), "strings.xml"))
            }
    }
}
