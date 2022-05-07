package com.andro.spreadi18ncore

import java.io.File

val File.dirs: Array<File>
    get() = this.listFiles { file -> file.isDirectory }

val File.files: Array<File>
    get() = this.listFiles { file -> file.isFile }


internal interface LocalizationDirectoriesFinder {
    fun findLocalizationDirectoriesIn(rootFile: File): List<TargetDirectory>
}

fun allDirsRecursively(parentFile: File): List<File> {
    return parentFile.dirs
            .map { file ->
                listOf(file) + allDirsRecursively(file)
            }
            .flatten()
}

@Suppress("ClassName")
internal class iOSLocalizationDirectoriesFinder: LocalizationDirectoriesFinder {
    override fun findLocalizationDirectoriesIn(rootFile: File): List<TargetDirectory> {
        return allDirsRecursively(rootFile)
                .filter { dir -> dir.name.endsWith(".lproj") }
                .map { TargetDirectory(it) }
    }
}

internal class AndroidLocalizationDirectoriesFinder: LocalizationDirectoriesFinder {
    override fun findLocalizationDirectoriesIn(rootFile: File): List<TargetDirectory> {
        return allDirsRecursively(rootFile)
                .filter { dir -> dir.name.startsWith("values") && dir.files.any { it.name == "strings.xml"} }
                .map { TargetDirectory(it) }
    }
}
