package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.excel.KeyCellNotFound
import com.andro.spreadi18ncore.excel.HeaderRowNotFound
import com.andro.spreadi18ncore.excel.KeyType
import com.andro.spreadi18ncore.excel.WorkbookOpeningError
import com.andro.spreadi18ncore.localization.LanguageTagExtractionError
import com.andro.spreadi18ncore.transfer.UnknownTransferError
import com.andro.spreadi18ncore.transfer.base.CanNotAccessLocalizationFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

class ErrorTests {

    @Test
    fun `LanguageTagExtractionError has not empty message`() {
        assertThat(LanguageTagExtractionError("tag candidate").message).isNotEmpty
    }

    @Test
    fun `UnknownTransferError has not empty message`() {
        assertThat(UnknownTransferError(RuntimeException("The world exploded")).message).isNotEmpty
    }

    @Test
    fun `HeaderRowNotFound has not empty message`() {
        assertThat(HeaderRowNotFound().message).isNotEmpty
    }

    @Test
    fun `ColumnNotFound has not empty message`() {
        assertThat(KeyCellNotFound(KeyType.Android).message).isNotEmpty
    }

    @Test
    fun `CanNotAccessLocalizationFile has not empty message`() {
        assertThat(CanNotAccessLocalizationFile(IOException("File not found")).message).isNotEmpty
    }

    @Test
    fun `WorkbookOpeningError has not empty message`() {
        assertThat(WorkbookOpeningError(Path.of("/tmp/sheet.xls"), IOException("File not found")).message).isNotEmpty
    }
}