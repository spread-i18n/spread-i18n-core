package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.valuetransformation.AndroidDefaultValueTransformation
import com.andro.spreadi18ncore.valuetransformation.CustomValueTransformation
import com.andro.spreadi18ncore.valuetransformation.iOSDefaultValueTransformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValueTransformationTests {

    @Test
    fun default_iOS_transformation() {
        assertThat(iOSDefaultValueTransformation().transform(""" "Be or not to be: %s" """))
                                                .isEqualTo("""\"Be or not to be: %@\"""")
    }

    @Test
    fun default_Android_transformation() {
        assertThat(AndroidDefaultValueTransformation().transform(""" "Me & you" are > than 'they' """))
                .isEqualTo("""\"Me &amp; you\" are &gt; than \'they\'""")
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