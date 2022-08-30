package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.helpers.mockSheet
import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.sourcesheet.ImportException
import com.andro.spreadi18ncore.targetproject.LanguageTag
import com.andro.spreadi18ncore.targetproject.ProjectType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class HeaderRowTests {

    @Test
    fun `A headerRow is not found when any row does not have localization and project column`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │en       │pl       │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │         │         │
            └──────────────────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))
        assertThat(headerRow).isNull()
    }

    private val String.tag get() = LanguageTag.extractFromString(this)
    @Test
    fun `Finding a header row with expected language tags`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │         │         │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │en       │pl       │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """

        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        assertThat(headerRow.rowInDocument).isEqualTo(1)
        assertThat(headerRow.localeCells.map { it.languageTag }).
            hasSameElementsAs(listOf("en".tag, "pl".tag))
    }

    @Test
    fun `Finding a key column when platform keys are specified explicitly`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │Android Key│iOS Key   │en       │pl       │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = headerRow.columnIndexForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex.value).isEqualTo(0)
        val iOSKeyColumnIndex = headerRow.columnIndexForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumnIndex.value).isEqualTo(1)
    }

    @Test
    fun `Finding key column when key is specified generally by Identifier word`() {
        val sheetContent = """
            ┌───────────────────────────────┐
            │Identifier │en       │pl       │
            ├───────────────────────────────┤
            │           │         │         │
            └───────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = headerRow.columnIndexForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex.value).isEqualTo(0)
        val iOSKeyColumnIndex = headerRow.columnIndexForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumnIndex.value).isEqualTo(0)
    }

    @Test
    fun `Throwing exception when getting key column does not exist for a platform`() {
        val sheetContent = """
            ┌────────────────────────────┐
            │iOS Key │en       │pl       │
            ├────────────────────────────┤
            │        │         │         │
            └────────────────────────────┘
        """
        assertThatExceptionOfType(ImportException::class.java)
                .isThrownBy{
                    val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
                    headerRow.columnIndexForProjectType(ProjectType.Android)
                }
    }
}