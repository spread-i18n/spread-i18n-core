package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.importing.XmlAndroidTranslationKeyValueWriter
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.StringReader
import java.io.StringWriter


class XmlAndroidTranslationKeyValueWriterTest {
    @Test
    fun `Replaces values in Android string resource`() {

        val inXml = """<?xml version="1.0" encoding="UTF-8"?>
                <resources>
                    <string name="message_hello">Hello</string>
                    <string name="message_bye">Bye</string>
                </resources>""".trimIndent()
        val stringWriter = StringWriter()
        val writer = XmlAndroidTranslationKeyValueWriter(BufferedReader(StringReader(inXml)), BufferedWriter(stringWriter))
        writer.write(KeyValue("key1", "value1"))
        writer.write(KeyValue("key2", "value2"))
        writer.close()

        val outXml = stringWriter.toString()
        assertThat(outXml).contains("key1")
        assertThat(outXml).contains("value1")
        assertThat(outXml).contains("key2")
        assertThat(outXml).contains("value2")

        assertThat(outXml).doesNotContain("message_hello")
        assertThat(outXml).doesNotContain("Hello")
        assertThat(outXml).doesNotContain("message_bye")
        assertThat(outXml).doesNotContain("Bye")
    }

    @Test
    fun `Creates comments in an Android resource`() {

        val inXml = ""
        val stringWriter = StringWriter()
        val writer = XmlAndroidTranslationKeyValueWriter(BufferedReader(StringReader(inXml)), BufferedWriter(stringWriter))
        writer.write(KeyValue("//A comment", ""))
        writer.close()

        val outXml = stringWriter.toString()
        assertThat(outXml).contains("<!--A comment-->")
    }

    @Test
    fun `Creates non translatable attribute in an Android string resource`() {

        val inXml = ""
        val stringWriter = StringWriter()
        val writer = XmlAndroidTranslationKeyValueWriter(BufferedReader(StringReader(inXml)), BufferedWriter(stringWriter))
        writer.write(KeyValue("*celsius_symbol", "°C"))
        writer.close()

        val outXml = stringWriter.toString()
        assertThat(outXml).contains("name=\"celsius_symbol\" translatable=\"false\"")
    }
}