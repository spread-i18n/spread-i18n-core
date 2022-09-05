package com.andro.spreadi18ncore.integrationtests

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.helpers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `Import of translations from an excel file to an iOS project`() = iOSFixture("proj-i1") {
        //arrange
        with(structure) {
            withLocalizationFile("en") {}
            withLocalizationFile("fr") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌─────────────────────────────────────┐
            │Key              │en      │fr        │
            ├─────────────────────────────────────┤
            │btn_cancel_text  │Cancel  │Annuler   │
            ├─────────────────────────────────────┤
            │btn_apply_text   │Apply   │Appliquer │
            └─────────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(excelFilePath)

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

    @Test
    fun `Transformation and import of translations from an excel file to an Android project`() =
        androidFixture("proj-i2") {
            //arrange
            with(structure) {
                withLocalizationFile("en") {}
                withLocalizationFile("pl") {}
            }.create()

            NewExcelFile.onPath(excelFilePath).load(
                """
            ┌────────────────────────────────────┐
            │Key              │en        │pl     │
            ├────────────────────────────────────┤
            │x_times_text     │"%d times"│%d razy│
            └────────────────────────────────────┘
            """
            ).save()

            //act
            Project.onPath(projectPath)
                .import(from = excelFilePath, valueTransformations = mapOf("%d" to "%s", "\"" to "'"))

            //assert
            with(Project.onPath(projectPath).localeFile("en")) {
                assert(contains(KeyValue("x_times_text", "'%s times'")))
            }

            with(Project.onPath(projectPath).localeFile("pl")) {
                assert(contains(KeyValue("x_times_text", "%s razy")))
            }
        }
}

@DisplayName("Comments are present in an excel translation files")
internal class CommentsImportTests {
    @Test
    fun `Comments are imported to Android translations from an excel file`() = androidFixture("proj-i3") {
        //arrange
        with(structure) {
            withLocalizationFile("en") {}
            withLocalizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌──────────────────────────────────┐
            │Key              │en      │pl     │
            ├──────────────────────────────────┤
            │//Polite phrases │        │       │
            ├──────────────────────────────────┤
            │message_hello    │Hello   │Cześć  │
            └──────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(Comment("Polite phrases")))
        }

        with(Project.onPath(projectPath).localeFile("pl")) {
            assert(contains(Comment("Polite phrases")))
        }
    }

    @Test
    fun `Comments are imported to iOS translations from an excel file`() = iOSFixture("proj-i4") {
        //arrange
        with(structure) {
            withLocalizationFile("en") {}
            withLocalizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌──────────────────────────────────┐
            │Key              │en      │pl     │
            ├──────────────────────────────────┤
            │//Polite phrases │        │       │
            ├──────────────────────────────────┤
            │message_hello    │Hello   │Cześć  │
            └──────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(Comment("Polite phrases")))
        }

        with(Project.onPath(projectPath).localeFile("pl")) {
            assert(contains(Comment("Polite phrases")))
        }
    }
}

internal class NonTranslatableImportTest {
    @Test
    fun `Import non translatables from an excel file to an Android project`() = androidFixture("proj-i5") {
        //arrange
        with(structure) {
            withLocalizationFile("en") {}
            withLocalizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌────────────────────────────────────┐
            │Key                │en      │pl     │
            ├────────────────────────────────────┤
            │*celsius_symbol    │°C      │       │
            ├────────────────────────────────────┤
            │*fahrenheit_symbol │°F      │       │
            └────────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(KeyValue("*celsius_symbol", "°C")))
            assert(contains(KeyValue("*fahrenheit_symbol", "°F")))
        }
    }
}

//https://developer.android.com/guide/topics/resources/string-resource#FormattingAndStyling
class HTMLmarkupSupportTest {

    @Test
    fun `Html markup is preserved after translations import to an Android project`() = androidFixture("proj-i6") {
        //arrange
        with(structure) {
            withLocalizationFile("en") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌─────────────────────────────┐
            │Key      │en                 │
            ├─────────────────────────────┤
            │hello    │hello <b>World</b> │
            └─────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(KeyValue("hello", "hello <b>World</b>")))
        }
    }
}