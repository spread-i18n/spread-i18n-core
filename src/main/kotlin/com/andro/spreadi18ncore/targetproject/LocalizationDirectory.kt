package com.andro.spreadi18ncore.targetproject

import java.io.File
import java.nio.file.Path

internal data class LocalizationDirectory(val path: Path) {

    constructor(path: String) : this(Path.of(path))

    val file: File by lazy {
        path.toFile()
    }
}
