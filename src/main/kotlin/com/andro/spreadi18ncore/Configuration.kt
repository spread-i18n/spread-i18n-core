package com.andro.spreadi18ncore

import java.nio.file.Path

data class ImportConfiguration(
    val sourceFilePath: Path,
    val targetProjectPath: Path,
    val valueTransformationMap: Map<String, String>? = null,
)