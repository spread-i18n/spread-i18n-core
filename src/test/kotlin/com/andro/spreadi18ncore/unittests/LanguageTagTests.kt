package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.localization.LanguageTag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path

class LanguageTagTests {

    @Test
    fun `The values directory is treated as the location of default localization`() {
        with(LanguageTag.extractFromPath(Path.of("app/src/main/res/values"))) {
            assertThat(this).isEqualTo(LanguageTag.fromString("default"))
        }
    }

    @Test
    fun `Extracts a tag from an iOS localization directory containing a region`() {
        with(LanguageTag.extractFromPath(Path.of("project/pt-BR.lproj"))) {
            assertThat(this).isEqualTo(LanguageTag.fromString("pt-BR"))
        }
    }

    @Test
    fun `Extracts a tag from an Android localization directory containing a region`() {
        with(LanguageTag.extractFromPath(Path.of("app/src/main/res/values-pt-rBR"))) {
            assertThat(this).isEqualTo(LanguageTag.fromString("pt-BR"))
        }
    }

    @Test

    fun `Extracts a tag from an Android localization directory not containing a region`() {
        with(LanguageTag.extractFromPath(Path.of("app/src/main/res/values-pl"))) {
            assertThat(this).isEqualTo(LanguageTag.fromString("pl"))
        }
    }
}