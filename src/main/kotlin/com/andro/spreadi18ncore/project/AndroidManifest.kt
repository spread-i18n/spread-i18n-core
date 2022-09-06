package com.andro.spreadi18ncore.project

import java.nio.file.Path

internal object AndroidManifest {
    fun existsIn(path: Path): Boolean {
        return allDirsRecursively(path.toFile())
            .any { dir -> dir.files.any { it.name == "AndroidManifest.xml" } }
    }
}
