package com.andro.spreadi18ncore

import java.nio.file.Path

class Project private constructor(private val projectPath: Path){

    companion object {
        fun onPath(projectPath: Path): Project {
            return Project(projectPath)
        }
        fun onPath(projectPath: String): Project {
            return onPath(Path.of(projectPath))
        }
    }

    fun importFrom(filePath: String) {
        importFrom(Path.of(filePath))
    }

    fun importFrom(filePath: Path) {
        Import.perform(sourceFilePath = filePath, targetProjectPath = projectPath)
    }

    fun importUsing(configuration: ImportConfiguration) {
        Import.perform(configuration)
    }
}