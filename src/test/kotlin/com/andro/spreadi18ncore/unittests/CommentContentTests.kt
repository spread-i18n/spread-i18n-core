package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.transfer.exporting.XmlCommentExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class CommentContentTests {

    @Test
    fun `Extracts regular comment`() {

        val xmlComment = """<!--A comment-->"""
        assertThat(XmlCommentExtractor.extractComment(xmlComment)).isEqualTo("A comment")
    }

    @Test
    fun `Extracts comment without leading and trailing spaces`() {

        val xmlComment = """    <!-- A comment -->"""
        assertThat(XmlCommentExtractor.extractComment(xmlComment)).isEqualTo("A comment")
    }

    @Test
    fun `Extracts comment with special character`() {

        val xmlComment = """    <!-- This is symbol of silver: 🜛 -->"""
        assertThat(XmlCommentExtractor.extractComment(xmlComment)).isEqualTo("""This is symbol of silver: 🜛""")
    }
}