package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.iOSEscaping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValueTransformationTests {

    @Test
    fun `The iOS escaping escapes quotes`() {
        assertThat(iOSEscaping.escape(""""Be or not to be: %@"""")).isEqualTo("""\"Be or not to be: %@\"""")
    }

    @Test
    fun `The Android escaping escapes quotes and '&' characters`() {
        assertThat(AndroidEscaping.escape(""""Me & you" are better than 'they'"""))
                .isEqualTo("""\"Me &amp; you\" are better than \'they\'""")
    }

    @Test
    fun `The Android escaping does not replace the special html escaping characters`() {
        val html = """&lt;src=&quot;Hello.img&quot; /&gt;"""
        assertThat(AndroidEscaping.escape(html))
            .isEqualTo(html)
    }
    
    @Test
    fun `Custom transformation replaces characters given in map`() {
        val transformationMap = mapOf(
                "a" to "A",
                "b" to "B",
                "c" to "C")
        assertThat(CustomValueTransformation(transformationMap).transform("""abc"""))
                .isEqualTo("""ABC""")
    }
}