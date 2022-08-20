package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.filewriting.XmlAndroidTranslationKeyValueWriter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.StringReader
import java.io.StringWriter


class AndroidTranslationKeyValueWriter2Test {
    @Test
    fun `Replacing values in Android string resource`() {

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
    fun `xxx`() {

        val inXml = ""
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
    }
}