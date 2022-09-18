package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.project.iOSDevelopmentLanguageExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class iOSDevelopmentLanguageExtractorTests {

    @Test
    fun `Extracts development region from a line containing valid tag`() {
        with(iOSDevelopmentLanguageExtractor) {
            assertThat(extract("\t\tdevelopmentRegion = fr-CA;")!!.canonical).isEqualTo("fr-CA")
        }
    }

    @Test
    fun `Extracts null development region from a line not containing valid tag`() {
        with(iOSDevelopmentLanguageExtractor) {
            assertThat(extract("\t\tdevelopmentRegion;")).isNull()
        }
    }
}