package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.helpers.AndroidProjectStructure
import com.andro.spreadi18ncore.helpers.ExcelFile
import com.andro.spreadi18ncore.helpers.iOSProjectStructure
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import java.util.*

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
    fun `export of an iOS project translations to an excel file`() {

        val projectPath = Path.of("tmp/iOS/proj-x1")
        iOSProjectStructure(projectPath).localizationFile(Locale.ENGLISH) {
            translations {
                ("message_hello" to "Hello") + ("NSBluetoothPeripheralUsageDescription" to "Bluetooth needed")
            }
        }.localizationFile(Locale.FRENCH) {
            translations {
                ("message_hello" to "Bonjour") + ("NSBluetoothPeripheralUsageDescription" to "Autoriser le Bluetooth")
            }
        }.create()

        val destinationFilePath = Path.of("tmp/iOS/proj-x1.xls")

        Project.onPath(projectPath).export(destinationFilePath)

        ExcelFile.of(destinationFilePath).use { excelFile ->
            assert(excelFile.contains(key = "message_hello", value = "Hello"))
            assert(excelFile.contains(key = "message_hello", value = "Bonjour"))
            assert(excelFile.contains(key = "NSBluetoothPeripheralUsageDescription", value = "Bluetooth needed"))
            assert(excelFile.contains(key = "NSBluetoothPeripheralUsageDescription", value = "Autoriser le Bluetooth"))
        }
    }

    @Test
    fun `export of an Android project translations to an excel file`() {

        val projectPath = Path.of("tmp/Android/proj-x1")
        AndroidProjectStructure(projectPath).localizationFile(Locale.ENGLISH) {
            translations { ("message_hello" to "Hello") + ("message_bye" to "Bye") }
        }.localizationFile(Locale.FRENCH) {
            translations { ("message_hello" to "Bonjour") + ("message_bye" to "Adieu") }
        }.create()

        val destinationFilePath = Path.of("tmp/android/proj-x1.xls")

        Project.onPath(projectPath).export(destinationFilePath)

        ExcelFile.of(destinationFilePath).use { excelFile ->
            assertThat(excelFile.contains(key = "message_hello", value = "Hello")).isTrue
            assertThat(excelFile.contains(key = "message_hello", value = "Bonjour")).isTrue
            assertThat(excelFile.contains(key = "message_bye", value = "Bye")).isTrue
            assertThat(excelFile.contains(key = "message_bye", value = "Adieu")).isTrue
        }
    }
}