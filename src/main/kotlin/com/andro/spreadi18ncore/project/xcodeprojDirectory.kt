package com.andro.spreadi18ncore.project

import java.io.File
import java.nio.file.Path

@Suppress("ClassName")
internal object xcodeprojDirectory {
    fun existsIn(path: Path): Boolean {
        val isXcodeDir: (File) -> Boolean = {
            it.name.endsWith(".xcodeproj")
        }
        return (path.toFile().dirs.any { isXcodeDir(it) }) ||
                allDirsRecursively(path.toFile()).any { dir -> dir.dirs.any { isXcodeDir(it) } }
    }
}
