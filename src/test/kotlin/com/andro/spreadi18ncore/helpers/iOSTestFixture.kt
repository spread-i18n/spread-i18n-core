package com.andro.spreadi18ncore.helpers

import java.nio.file.Path


@Suppress("TestFunctionName")
internal fun iOSFixture(name: String, block: iOSTestFixture.() -> Unit) {
    iOSTestFixture(name, block)
}

@Suppress("ClassName")
internal class iOSTestFixture(name: String, block: iOSTestFixture.() -> Unit) {

    val excelFilePath: Path = Path.of("tmp/iOS/$name.xls")
    val projectPath: Path = Path.of("tmp/iOS/$name")
    val structure: iOSProjectStructure get() = iOSProjectStructure(projectPath)
    init {
        projectPath.toFile().deleteRecursively()
        excelFilePath.toFile().delete()
        block()
    }
}
