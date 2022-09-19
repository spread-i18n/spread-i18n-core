package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.transfer.exporting.PlainAndroidTranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.indicatesNonTranslatable
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.StringReader

internal fun TranslationKeyValueReader.readAll(valueTransformation: ValueTransformation? = null): List<KeyValue> {
    val keyValues = mutableListOf<KeyValue>()
    var keyValue = read(valueTransformation)
    while (keyValue != null) {
        keyValues.add(keyValue)
        keyValue = read(valueTransformation)
    }
    close()
    return keyValues
}

internal val List<KeyValue>.keys: List<String> get() = map { it.key }
internal val List<KeyValue>.values: List<String> get() = map { it.value }

class PlainAndroidTranslationKeyValueReaderTest {

    @Test
    fun `Reads all values from an Android string resource`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="one">1</string>
                <string name="two">2</string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))
        Assertions.assertThat(reader.readAll()).containsExactly(KeyValue("one", "1"), KeyValue("two", "2"))
    }

    @Test
    fun `Reads comments from an Android string resource`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <!--A comment-->
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))
        Assertions.assertThat(reader.readAll().keys).contains("//A comment")
    }

    @Test
    fun `Reads non translatable from an Android string resource`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="celsius_symbol" translatable="false">°C</string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))
        with(reader.readAll().keys) {
            Assertions.assertThat(this.count()).isEqualTo(1)
            Assertions.assertThat(this).allMatch { it.indicatesNonTranslatable }
        }
    }

    @Test
    fun `Transforms translations from an Android string resource`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="hello">Hello %s</string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))
        val transformation = CustomValueTransformation("%s" to "@")
        with(reader.readAll(transformation).values) {
            Assertions.assertThat(this).containsExactly( "Hello @" )
        }
    }

    @Test
    fun `Android 'keyValue' reader reverses Android escaping`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="hello">Hello \'%s\'</string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))
        with(reader.readAll().values) {
            Assertions.assertThat(this).containsExactly( "Hello '%s'" )
        }
    }

    @Test
    fun `Android 'keyValue' reader reads multiline xml strings`() {

        val html = """
                <html>
                    <head></head>
                    <body>
                       Hello people
                    </body>
                </html>
            """.trimIndent()

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="app_intro_html">
                   $html 
                </string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))

        with(reader.readAll().values) {
            Assertions.assertThat(this).containsExactly(html)
        }
    }

    @Test
    fun `Android 'keyValue' reader reads multiline escaped xml strings`() {

        val html = """
                &lt;html&gt;
                    &lt;head&gt;&lt;/head&gt;
                    &lt;body&gt;
                        &lt;src=&quotHello.img&quot /&gt;
                    &lt;/body&gt;
                &lt;/html&gt;
            """.trimIndent()

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string name="app_intro_html">
                   $html 
                </string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))

        with(reader.readAll().values) {
            Assertions.assertThat(this).containsExactly(html)
        }
    }

    @Test
    fun `Android reader reads array values`() {

        val androidTranslationContent = """
            <?xml version="1.0"?>
            <resources>
                <string-array name="weekdays">
                    <item>Monday</item>
                    <item>Tuesday</item>
                    <item>Wednesday</item>
                    <item>Saturday &amp; Sunday</item>
                </string-array>
                <string name="hello">Hello</string>
            </resources>
        """.trimIndent()
        val stringReader = StringReader(androidTranslationContent)
        val reader = PlainAndroidTranslationKeyValueReader(BufferedReader(stringReader))

        with(reader.readAll()) {
            Assertions.assertThat(this).containsExactly(
                KeyValue("weekdays-array", "Monday\nTuesday\nWednesday\nSaturday & Sunday"),
                KeyValue("hello", "Hello"),
            )
        }
    }
}