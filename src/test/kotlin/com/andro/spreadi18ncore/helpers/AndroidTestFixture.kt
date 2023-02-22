package com.andro.spreadi18ncore.helpers

import java.nio.file.Path

internal fun androidFixture(projectName: String, block: AndroidTestFixture.()->Unit) {
    AndroidTestFixture(projectName, block)
}

internal class AndroidTestFixture(projectName: String, block: AndroidTestFixture.()->Unit) {

    val excelFilePath: Path = Path.of("tmp/android/$projectName.xls")
    val projectPath: Path = Path.of("tmp/android/$projectName")
    val projectStructure: AndroidProjectStructure get() = AndroidProjectStructure(projectPath)
    init {
        projectPath.toFile().deleteRecursively()
        excelFilePath.toFile().delete()
        block()
    }
}
