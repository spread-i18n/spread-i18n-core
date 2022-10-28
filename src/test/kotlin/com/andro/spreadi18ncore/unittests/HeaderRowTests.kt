package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.excel.HeaderRow
import com.andro.spreadi18ncore.helpers.mockSheet
import com.andro.spreadi18ncore.localization.LanguageTag
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.transfer.TransferException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class HeaderRowTests {

    @Test
    fun `A header row is not found when any row does not have localization and project column`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │default  │pl       │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │         │         │
            └──────────────────────────────────────────┘
        """
        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))
        assertThat(headerRow).isNull()
    }

    private val String.tag get() = LanguageTag.fromString(this)
    @Test
    fun `Finds a header row with the expected language tags`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │         │         │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │default  │pl       │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """

        val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
        assertThat(headerRow.rowInDocument).isEqualTo(1)
        assertThat(headerRow.localeCells.map { it.languageTag }).
            hasSameElementsAs(listOf("default".tag, "pl".tag))
    }

    @Test
    fun `Key column indices are found when platform keys are specified explicitly`() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │Android Key│iOS Key   │default  │pl       │
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
    fun `A key column index is found when a key is specified generally by the Identifier special word`() {
        val sheetContent = """
            ┌───────────────────────────────┐
            │Identifier │default  │pl       │
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
    fun `An instance of the TransferException is thrown when getting a non existent key column`() {
        val sheetContent = """
            ┌────────────────────────────┐
            │iOS Key │default  │pl       │
            ├────────────────────────────┤
            │        │         │         │
            └────────────────────────────┘
        """
        assertThatExceptionOfType(TransferException::class.java)
                .isThrownBy{
                    val headerRow = HeaderRow.findIn(mockSheet(sheetContent))!!
                    headerRow.columnIndexForProjectType(ProjectType.Android)
                }
    }
}