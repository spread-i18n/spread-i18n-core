package com.andro.spreadi18ncore.integrationtests

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.helpers.*
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `Imports translations from an excel file to an iOS project`()
        = iOSFixture(projectName = "proj-i1") {
        //arrange
        with(projectStructure) {
            localizationFile("en") {}
            localizationFile("fr") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌─────────────────────────────────────┐
            │Key              │default │fr        │
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
    fun `Transforms and imports translations from an excel file to an Android project`()
        = androidFixture(projectName = "proj-i2") {
            //arrange
            with(projectStructure) {
                localizationFile("default") {}
                localizationFile("pl") {}
            }.create()

            NewExcelFile.onPath(excelFilePath).load(
                """
            ┌────────────────────────────────────┐
            │Key              │default   │pl     │
            ├────────────────────────────────────┤
            │x_times_text     │"%d times"│%d razy│
            └────────────────────────────────────┘
            """
            ).save()

            //act
            Project.onPath(projectPath)
                .import(from = excelFilePath, valueTransformations = mapOf("%d" to "%s", "\"" to "'"))

            //assert
            with(Project.onPath(projectPath).localeFile("default")) {
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
    fun `Comments are imported to Android translations from an excel file`()
        = androidFixture(projectName = "proj-i3") {
        //arrange
        with(projectStructure) {
            localizationFile("default") {}
            localizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌──────────────────────────────────┐
            │Key              │default │pl     │
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
        with(Project.onPath(projectPath).localeFile("default")) {
            assert(contains(Comment("Polite phrases")))
        }

        with(Project.onPath(projectPath).localeFile("pl")) {
            assert(contains(Comment("Polite phrases")))
        }
    }

    @Test
    fun `Comments are imported to iOS translations from an excel file`()
        = iOSFixture(projectName = "proj-i4") {
        //arrange
        with(projectStructure) {
            localizationFile("en") {}
            localizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌──────────────────────────────────┐
            │Key              │default │pl     │
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
    fun `Imports non translatables from an excel file to an Android project`()
        = androidFixture(projectName = "proj-i40") {
        //arrange
        with(projectStructure) {
            localizationFile("default") {}
            localizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌────────────────────────────────────┐
            │Key                │default │pl     │
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
        with(Project.onPath(projectPath).localeFile("default")) {
            assert(contains(KeyValue("*celsius_symbol", "°C")))
            assert(contains(KeyValue("*fahrenheit_symbol", "°F")))
        }
    }

    @Test
    fun `Non translatable strings are not present in non the default resource after import`()
        = androidFixture(projectName = "proj-i41") {
        //arrange
        with(projectStructure) {
            localizationFile("default") {}
            localizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌────────────────────────────────────┐
            │Key                │default │pl     │
            ├────────────────────────────────────┤
            │*celsius_symbol    │°C      │       │
            ├────────────────────────────────────┤
            │hello_message      │Hello   │Cześć  │
            └────────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).rawLocaleFile("default")) {
            assert(containsInLine("celsius_symbol", "°C"))
            assert(containsInLine("hello_message", "Hello"))
        }

        with(Project.onPath(projectPath).rawLocaleFile("pl")) {
            assertFalse(containsInLine("celsius_symbol"))
            assert(containsInLine("hello_message", "Cześć"))
        }
    }
}

//https://developer.android.com/guide/topics/resources/string-resource#FormattingAndStyling
internal class HtmlMarkupSupportTest {

    @Test
    fun `A html markup is preserved after import of translations to an Android project`()
        = androidFixture(projectName = "proj-i50") {
        //arrange
        with(projectStructure) {
            localizationFile("default") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌─────────────────────────────┐
            │Key      │default            │
            ├─────────────────────────────┤
            │hello    │hello <b>World</b> │
            └─────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("default")) {
            assert(contains(KeyValue("hello", "hello <b>World</b>")))
        }
    }
}

internal class CharacterEscapingTest {

    //https://developer.android.com/guide/topics/resources/string-resource#escaping_quotes
    @Test
    fun `Special characters are escaped after import to an Android project`()
        = androidFixture(projectName = "proj-i60") {
        //arrange
        with(projectStructure) {
            localizationFile("default") {}
        }.create()

        with(NewExcelFile.onPath(excelFilePath)) {
            writeRow("key", "default")
            writeRow("new_line", "new\nline")
            writeRow("tabulation", "tab\ttab")
            writeRow("question", "wtf?")
            writeRow("at", "john.doe@gmail.com")
            writeRow("single_quote", "5 o'clock")
            writeRow("double_quote", """Hello "World" """)
            save()
        }

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).rawLocaleFile("default")) {
            assert(containsInLine("new_line", """new\nline"""))
            assert(containsInLine("tabulation", """tab\ttab"""))
            assert(containsInLine("question", """wtf\?"""))
            assert(containsInLine("at", """john.doe\@gmail.com"""))
            assert(containsInLine("single_quote", """5 o\'clock"""))
            assert(containsInLine("double_quote", """Hello \"World\""""))
        }
    }

    //To test iOS escaping
}

@Suppress("ClassName")
internal class iOSDefaultTranslationImportTests {

    @Test
    fun `Default translations are imported to 'development language' from an excel file`()
            = iOSFixture(projectName = "proj-i70", developmentLanguage = "pl") {
        //arrange
        with(projectStructure) {
            localizationFile("en") {}
            localizationFile("pl") {}
        }.create()

        NewExcelFile.onPath(excelFilePath).load(
            """
            ┌──────────────────────────────────┐
            │Key              │en      │default│
            ├──────────────────────────────────┤
            │message_bye      │Bye     │Żegnaj │
            ├──────────────────────────────────┤
            │message_hello    │Hello   │Cześć  │
            └──────────────────────────────────┘
            """
        ).save()

        //act
        Project.onPath(projectPath).import(from = excelFilePath)

        //assert
        with(Project.onPath(projectPath).localeFile("pl")) {
            assert(contains(KeyValue("message_bye", "Żegnaj")))
            assert(contains(KeyValue("message_hello", "Cześć")))
        }

        with(Project.onPath(projectPath).localeFile("en")) {
            assert(contains(KeyValue("message_bye", "Bye")))
            assert(contains(KeyValue("message_hello", "Hello")))
        }
    }
}