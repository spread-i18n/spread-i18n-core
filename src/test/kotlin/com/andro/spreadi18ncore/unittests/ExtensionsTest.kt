package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.mappedFirstOrNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExtensionsTest {

    @Test
    fun `Returns first not null mapped value`() {
        assertThat(
            listOf(1,2,3,4,5).asSequence().mappedFirstOrNull { if (it>=3) it.toString() else null }
        ).isEqualTo("3")
    }
}