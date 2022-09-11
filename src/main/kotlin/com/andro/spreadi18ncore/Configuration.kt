package com.andro.spreadi18ncore

import java.nio.file.Path

data class Configuration(
    val filePath: Path,
    val projectPath: Path,
    val valueTransformationMap: Map<String, String>? = null,
)