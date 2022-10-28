package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.exporting.XmlKeyValueExtractor
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValueExtractionTests {

    @Test
    fun `Extracts a string value from a single line xml`() {
        with(XmlKeyValueExtractor) {
            assertThat(extract("\t<string name=\"foo\">bar</string>"))
                .isEqualTo(KeyValue("foo", "bar"))
        }
    }

    @Test
    fun `Extracts a string value from a multiline xml`() {
        with(XmlKeyValueExtractor) {
            assertThat(extract("\t<string name=\"key\">foo\nbar</string>"))
                .isEqualTo(KeyValue("key", "foo\nbar"))
        }
    }

    @Test
    fun `Extract a html value from a multiline xml`() {
        with(XmlKeyValueExtractor) {
            assertThat(extract("\t<string name=\"key\"><html>\n\t<body>\t\n</body>\n</html></string>"))
                .isEqualTo(KeyValue("key", "<html>\n\t<body>\t\n</body>\n</html>"))
        }
    }
}