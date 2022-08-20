package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.helpers.AndroidProjectStructure
import com.andro.spreadi18ncore.helpers.ExistingExcelFile
import com.andro.spreadi18ncore.helpers.iOSProjectStructure
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

internal operator fun Pair<String, String>.plus(other: Pair<String, String>): List<KeyValue> {
    return listOf(KeyValue(this.first, this.second), KeyValue(other.first, other.second))
}

class ExportTests {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            File("tmp").deleteRecursively()
        }
    }

    @Test
    fun `Export of translations from an iOS project translations to an excel file`() {

        val projectPath = Path.of("tmp/iOS/proj-x1")
        iOSProjectStructure(projectPath)
            .withLocalizationFile("en") {
                withTranslations {
                    ("message_hello" to "Hello") + ("NSBluetoothPeripheralUsageDescription" to "Bluetooth needed")
                }
            }.withLocalizationFile("fr") {
                withTranslations {
                    ("message_hello" to "Bonjour") + ("NSBluetoothPeripheralUsageDescription" to "Autoriser le Bluetooth")
                }
            }.create()

        val destinationFilePath = Path.of("tmp/iOS/proj-x1.xls")

        Project.onPath(projectPath).export(destinationFilePath)

        ExistingExcelFile.onPath(destinationFilePath).use { excelFile ->
            assert(excelFile.containsInRow("key", "en", "fr"))
            assert(excelFile.containsInRow("message_hello", "Hello", "Bonjour"))
            assert(
                excelFile.containsInRow(
                    "NSBluetoothPeripheralUsageDescription", "Bluetooth needed", "Autoriser le Bluetooth"
                )
            )
        }
    }

    @Test
    fun `Export of translations from an Android project translations to an excel file`() {

        val projectPath = Path.of("tmp/Android/proj-x1")
        AndroidProjectStructure(projectPath)
            .withLocalizationFile("en") {
                withTranslations { ("message_hello" to "Hello") + ("message_bye" to "Bye") }
            }.withLocalizationFile("fr") {
                withTranslations { ("message_hello" to "Bonjour") + ("message_bye" to "Adieu") }
            }.create()

        val destinationFilePath = Path.of("tmp/android/proj-x1.xls")

        Project.onPath(projectPath).export(destinationFilePath)

        ExistingExcelFile.onPath(destinationFilePath).use { excelFile ->

            assert(excelFile.containsInRow("key", "en", "fr"))
            assert(excelFile.containsInRow("message_hello", "Hello", "Bonjour"))
            assert(excelFile.containsInRow("message_bye", "Bye", "Adieu"))
        }
    }
}