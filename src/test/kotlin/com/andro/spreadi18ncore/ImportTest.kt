package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.helpers.NewExcelFile
import com.andro.spreadi18ncore.helpers.iOSProjectStructure
import com.andro.spreadi18ncore.helpers.localeFile
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

class ImportTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            File("tmp").deleteRecursively()
        }
    }

    @Test
    fun `Import of translations from an excel file to an iOS project`() {
        //arrange
        val projectPath = Path.of("tmp/iOS/proj-x2")
        iOSProjectStructure(projectPath)
            .withLocalizationFile("en") {}
            .withLocalizationFile("fr") {}
            .create()

        val sourceFilePath = Path.of("tmp/iOS/proj-x2.xls")
        NewExcelFile.onPath(sourceFilePath).load ("""
            ┌─────────────────────────────────────┐
            │Key              │en      │fr        │
            ├─────────────────────────────────────┤
            │btn_cancel_text  │Cancel  │Annuler   │
            ├─────────────────────────────────────┤
            │btn_apply_text   │Apply   │Appliquer │
            └─────────────────────────────────────┘
            """).save()

        //act
        Project.onPath(projectPath).import(sourceFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(KeyValue("btn_cancel_text", "Cancel")))
            assert(contains(KeyValue("btn_apply_text", "Apply")))
        }

        with(Project.onPath(projectPath).localeFile("fr")) {
            assert(contains(KeyValue("btn_cancel_text", "Annuler")))
            assert(contains(KeyValue("btn_apply_text", "Appliquer")))
        }
    }
}