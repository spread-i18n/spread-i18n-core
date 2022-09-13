package com.andro.spreadi18ncore.helpers

import java.nio.file.Path


@Suppress("TestFunctionName")
internal fun iOSFixture(name: String, developmentLanguage: String = "en", block: iOSTestFixture.() -> Unit) {
    iOSTestFixture(name, developmentLanguage, block)
}

@Suppress("ClassName")
internal class iOSTestFixture(name: String, private val developmentLanguage: String, block: iOSTestFixture.() -> Unit) {

    val excelFilePath: Path = Path.of("tmp/iOS/$name.xls")
    val projectPath: Path = Path.of("tmp/iOS/$name")
    val structure: iOSProjectStructure get() = iOSProjectStructure(projectPath, developmentLanguage)
    init {
        projectPath.toFile().deleteRecursively()
        excelFilePath.toFile().delete()
        block()
    }
}
