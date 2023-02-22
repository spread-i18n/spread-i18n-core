package com.andro.spreadi18ncore.helpers

import java.nio.file.Path


@Suppress("TestFunctionName")
internal fun iOSFixture(projectName: String, developmentLanguage: String = "en", block: iOSTestFixture.() -> Unit) {
    iOSTestFixture(projectName, developmentLanguage, block)
}

@Suppress("ClassName")
internal class iOSTestFixture(projectName: String, private val developmentLanguage: String, block: iOSTestFixture.() -> Unit) {

    val excelFilePath: Path = Path.of("tmp/iOS/$projectName.xls")
    val projectPath: Path = Path.of("tmp/iOS/$projectName")
    val projectStructure: iOSProjectStructure get() = iOSProjectStructure(projectPath, developmentLanguage)
    init {
        projectPath.toFile().deleteRecursively()
        excelFilePath.toFile().delete()
        block()
    }
}
