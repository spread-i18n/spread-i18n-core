package com.andro.spreadi18ncore.project

import java.io.File
import java.nio.file.Path

@Suppress("ClassName")
internal object pbxprojFile {

    private val File.isXcodeProjectDirectory: Boolean
        get() {
            return name.endsWith(".xcodeproj") && (name != "Pods.xcodeproj")
        }

    private val File.isXcodeProjectFile: Boolean
        get() {
            return name == "project.pbxproj"
        }
    fun findPathIn(path: Path): Path? {
        return allDirsRecursively(path.toFile())
            .filter { it.isXcodeProjectDirectory }
            .map { it.files.toList() }
            .flatten()
            .firstOrNull { it.isXcodeProjectFile }?.toPath()
    }
    fun existsIn(path: Path): Boolean {
        return findPathIn(path) != null
    }
}
