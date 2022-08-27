package com.andro.spreadi18ncore.helpers

import java.nio.file.Path

internal fun androidFixture(name: String, block: AndroidTestFixture.()->Unit) {
    AndroidTestFixture(name, block)
}

internal class AndroidTestFixture(name: String, block: AndroidTestFixture.()->Unit) {

    val excelFilePath: Path = Path.of("tmp/android/$name.xls")
    val projectPath: Path = Path.of("tmp/android/$name")
    val structure: AndroidProjectStructure get() = AndroidProjectStructure(projectPath)
    init {
        projectPath.toFile().deleteRecursively()
        excelFilePath.toFile().delete()
        block()
    }
}
