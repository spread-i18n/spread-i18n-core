package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.importing.iOSValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValueTransformationTests {

    @Test
    fun default_iOS_transformation() {
        assertThat(iOSValueTransformation.transform(""" "Be or not to be: %s" """))
                                                .isEqualTo("""\"Be or not to be: %@\"""")
    }

    @Test
    fun default_Android_transformation() {
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