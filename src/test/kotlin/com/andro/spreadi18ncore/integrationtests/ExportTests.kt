package com.andro.spreadi18ncore.integrationtests

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.helpers.ExistingExcelFile
import com.andro.spreadi18ncore.helpers.androidFixture
import com.andro.spreadi18ncore.helpers.iOSFixture
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.transfer.withNonTranslatableIndicator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal operator fun Pair<String, String>.plus(other: Pair<String, String>): List<KeyValue> {
    return listOf(KeyValue(this.first, this.second), KeyValue(other.first, other.second))
}

internal operator fun List<KeyValue>.plus(other: Pair<String, String>): List<KeyValue> {
    return toMutableList().apply {
        add(KeyValue(other.first, other.second))
    }
}

class ExportTests {

    @Test
    fun `Exports translations from an iOS project to an excel file`()
        = iOSFixture(projectName = "proj-e1") {

        with(projectStructure) {
            localizationFile("en") {
                translations {
                    ("message_hello" to "Hello \"World\"") + ("NSBluetoothPeripheralUsageDescription" to "Bluetooth needed")
                }
            }
            localizationFile("fr") {
                translations {
                    ("message_hello" to "Bonjour") + ("NSBluetoothPeripheralUsageDescription" to "Autoriser le Bluetooth")
                }
            }
        }.create()

        Project.onPath(projectPath).export(to = excelFilePath)

        ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
            assert(excelFile.containsInRow("key", "default", "fr"))
            assert(excelFile.containsInRow("message_hello", "Hello \"World\"", "Bonjour"))
            assert(
                excelFile.containsInRow(
                    "NSBluetoothPeripheralUsageDescription", "Bluetooth needed", "Autoriser le Bluetooth"
                )
            )
        }
    }

    @Test
    fun `Exports translations from an Android project to an excel file`()
        = androidFixture(projectName = "proj-e2") {

            with(projectStructure) {
                localizationFile("default") {
                    translations { ("message_hello" to "Hello") + ("message_bye" to "Bye") }
                }.localizationFile("fr") {
                    translations { ("message_hello" to "Bonjour") + ("message_bye" to "Adieu") }
                }
            }.create()

            Project.onPath(projectPath).export(to = excelFilePath)

            ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
                assert(excelFile.containsInRow("key", "default", "fr"))
                assert(excelFile.containsInRow("message_hello", "Hello", "Bonjour"))
                assert(excelFile.containsInRow("message_bye", "Bye", "Adieu"))
            }
        }

    @Test
    fun `Transforms and exports translations from an Android project to an excel file`()
        = androidFixture(projectName = "proj-e3") {

            with(projectStructure) {
                localizationFile("pl") {
                    translations { ("message_hello" to "Hello %@ & %s") + ("message_bye" to "\"Bye %@\"") }
                }
            }.create()

            val valueTransformations = mapOf("%@" to "%s", "\"" to "")
            Project.onPath(projectPath).export(to = excelFilePath, valueTransformations = valueTransformations)

            ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
                assert(excelFile.containsInRow("key", "pl"))
                assert(excelFile.containsInRow("message_hello", "Hello %s & %s"))
                assert(excelFile.containsInRow("message_bye", "Bye %s"))
            }
        }

    @Test
    fun `Exports Android translations from the 'default' values directory to an excel file`()
        = androidFixture(projectName = "proj-e4") {

            with(projectStructure) {
                val defaultLanguageTag = "default"
                localizationFile(defaultLanguageTag) {
                    translations { ("message_hello" to "Hello") + ("message_bye" to "Bye") }
                }
            }.create()

            Project.onPath(projectPath).export(to = excelFilePath)

            ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
                assert(excelFile.containsInRow("key", "default"))
                assert(excelFile.containsInRow("message_hello", "Hello"))
                assert(excelFile.containsInRow("message_bye", "Bye"))
            }
        }
}

@DisplayName("Comments are present in project's translation files")
internal class CommentsExportTests {
    @Test
    fun `Comments are exported from Android translations to an excel file`()
        = androidFixture(projectName = "proj-e5") {
        with(projectStructure) {
            localizationFile("default") {
                translations { ("//Polite phrases" to "") + ("message_hello" to "Hello") + ("message_bye" to "bye") }
            }
        }.create()

        Project.onPath(projectPath).export(to = excelFilePath)

        ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
            assert(excelFile.containsInRow("//Polite phrases"))
        }
    }

    @Test
    fun `Comments are exported from iOS translations to an excel file`()
        = iOSFixture(projectName = "proj-e6") {
        with(projectStructure) {
            localizationFile("default") {
                translations { ("//Polite phrases" to "") + ("message_hello" to "Hello") + ("message_bye" to "bye") }
            }
        }.create()

        Project.onPath(projectPath).export(to = excelFilePath)

        ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
            assert(excelFile.containsInRow("//Polite phrases"))
        }
    }
}

internal class NonTranslatableExportTest {
    @Test
    fun `Exports non translatables from an Android project to an excel file`()
        = androidFixture(projectName = "proj-e7") {
        with(projectStructure) {
            localizationFile("default") {
                translations { ("celsius_symbol".withNonTranslatableIndicator to "°C") +
                        ("fahrenheit_symbol".withNonTranslatableIndicator to "°F") }
            }
        }.create()

        Project.onPath(projectPath).export(to = excelFilePath)

        ExistingExcelFile.onPath(excelFilePath).use { excelFile ->
            assert(excelFile.containsInRow("celsius_symbol".withNonTranslatableIndicator, "°C"))
            assert(excelFile.containsInRow("fahrenheit_symbol".withNonTranslatableIndicator, "°F"))
        }
    }
}