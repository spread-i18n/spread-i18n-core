import internal.ConfigRow
import internal.ImportException
import internal.ProjectType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class ConfigurationTests {

    @Test
    fun does_not_find_a_configRow_when_any_row_does_not_have_localisation_and_project_column() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │English  │Polish   │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │         │         │
            └──────────────────────────────────────────┘
        """
        val configRow = ConfigRow.findIn(mockSheet(sheetContent))
        assertThat(configRow).isNull()
    }

    @Test
    fun finding_configRow_with_expected_translation_source_columns() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │           │          │         │         │
            ├──────────────────────────────────────────┤
            │Android Key│iOS Key   │English  │Polish   │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """

        val configRow = ConfigRow.findIn(mockSheet(sheetContent))!!
        assertThat(configRow.rowInDocument).isEqualTo(1)
        assertThat(configRow.sourceColumns.map { it.title }).hasSameElementsAs(listOf("English", "Polish"))
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
        val configRow = ConfigRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = configRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex).isEqualTo(0)
        val iOSKeyColumnIndex = configRow.indexOfTranslationKeyColumnForProjectType(ProjectType.iOS)
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
        val configRow = ConfigRow.findIn(mockSheet(sheetContent))!!
        val androidKeyColumnIndex = configRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumnIndex).isEqualTo(0)
        val iOSKeyColumnIndex = configRow.indexOfTranslationKeyColumnForProjectType(ProjectType.iOS)
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
                    val configRow = ConfigRow.findIn(mockSheet(sheetContent))!!
                    configRow.indexOfTranslationKeyColumnForProjectType(ProjectType.Android)
                }
    }
}