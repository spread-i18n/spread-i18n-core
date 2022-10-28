package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.project.iOSDevelopmentLanguageExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class iOSDevelopmentLanguageExtractorTests {

    @Test
    fun `Extracts a development region from a line containing a valid tag`() {
        with(iOSDevelopmentLanguageExtractor) {
            assertThat(extract("\t\tdevelopmentRegion = fr-CA;")!!.canonical).isEqualTo("fr-CA")
        }
    }

    @Test
    fun `A development region is not extracted when a line does not contain a valid tag`() {
        with(iOSDevelopmentLanguageExtractor) {
            assertThat(extract("\t\tdevelopmentRegion;")).isNull()
        }
    }
}