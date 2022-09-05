package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.export.withCommentIndicator
import com.andro.spreadi18ncore.export.withNonTranslatableIndicator
import com.andro.spreadi18ncore.filewriting.PlainAndroidTranslationKeyValueWriter
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.BufferedWriter
import java.io.StringWriter

class PlainAndroidTranslationKeyValueWriterTest {

    @Test
    fun `Creates comments in an Android string resource`() {

        val stringWriter = StringWriter()
        val writer = PlainAndroidTranslationKeyValueWriter(BufferedWriter(stringWriter))
        writer.write(KeyValue("A comment".withCommentIndicator, ""))
        writer.close()

        val outXml = stringWriter.toString()
        Assertions.assertThat(outXml).contains("<!--A comment-->")
    }

    @Test
    fun `Creates non translatable attribute in an Android string resource`() {

        val stringWriter = StringWriter()
        val writer = PlainAndroidTranslationKeyValueWriter(BufferedWriter(stringWriter))
        writer.write(KeyValue("celsius_symbol".withNonTranslatableIndicator, "°C"))
        writer.close()

        val outXml = stringWriter.toString()
        Assertions.assertThat(outXml).contains("name=\"celsius_symbol\" translatable=\"false\"")
    }
}