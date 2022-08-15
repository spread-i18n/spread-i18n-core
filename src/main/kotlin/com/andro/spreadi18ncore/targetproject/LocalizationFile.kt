package com.andro.spreadi18ncore.targetproject

import java.nio.file.Path
import java.util.*

internal data class LocalizationFile(val path: Path) {
    private val directory: LocalizationDirectory by lazy { LocalizationDirectory(path) }
}