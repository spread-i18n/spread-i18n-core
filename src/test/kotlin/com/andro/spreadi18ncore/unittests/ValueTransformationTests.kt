package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.iOSEscaping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValueTransformationTests {

    @Test
    fun `iOS escaping`() {
        assertThat(iOSEscaping.escape(""""Be or not to be: %@"""")).isEqualTo("""\"Be or not to be: %@\"""")
    }

    @Test
    fun `Android escaping`() {
        assertThat(AndroidEscaping.escape(""""Me & you" are better than 'they'"""))
                .isEqualTo("""\"Me &amp; you\" are better than \'they\'""")
    }
    
    @Test
    fun custom_transformation() {
        val transformationMap = mapOf(
                "a" to "A",
                "b" to "B",
                "c" to "C")
        assertThat(CustomValueTransformation(transformationMap).transform("""abc"""))
                .isEqualTo("""ABC""")
    }
}