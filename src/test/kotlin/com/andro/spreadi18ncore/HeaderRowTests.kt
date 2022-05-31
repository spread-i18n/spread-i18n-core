package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.importing.ImportException
import com.andro.spreadi18ncore.targetproject.ProjectType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class HeaderRowTests {

    @Test
    fun does_not_find_a_headerRow_when_any_row_does_not_have_localisation_and_project_column() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │English  │Polish   │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │         │         │
            └──────────────────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))
        assertThat(headerRow).isNull()
    }

    @Test
    fun finding_headerRow_with_expected_translation_source_columns() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │         │         │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │English  │Polish   │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """

        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        assertThat(headerRow.rowInDocument).isEqualTo(1)
        assertThat(headerRow.localeCells.map { it.text }).hasSameElementsAs(listOf("English", "Polish"))
    }

    @Test
    fun finding_translation_key_column_when_columns_are_specified_explicitly() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │Android Key│iOS Key   │English  │Polish   │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = headerRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex).isEqualTo(0)
        val iOSKeyColumnIndex = headerRow.indexOfTranslationKeyColumnForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumnIndex).isEqualTo(1)
    }

    @Test
    fun finding_translation_key_column_when_column_is_specified_generally() {
        val sheetContent = """
            ┌───────────────────────────────┐
            │Identifier │English  │Polish   │
            ├───────────────────────────────┤
            │           │         │         │
            └───────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = headerRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex).isEqualTo(0)
        val iOSKeyColumnIndex = headerRow.indexOfTranslationKeyColumnForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumnIndex).isEqualTo(0)
    }

    @Test
    fun throwing_exception_when_getting_translation_column_for_not_existing_project() {
        val sheetContent = """
            ┌────────────────────────────┐
            │iOS Key │English  │Polish   │
            ├────────────────────────────┤
            │        │         │         │
            └────────────────────────────┘
        """
        assertThatExceptionOfType(ImportException::class.java)
                .isThrownBy{
                    val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
                    headerRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
                }
    }
}