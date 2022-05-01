import internal.ConfigRowFinder
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
        val configRow = ConfigRowFinder.findConfigRowIn(mockSheet(sheetContent))
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

        val configRow = ConfigRowFinder.findConfigRowIn(mockSheet(sheetContent))!!
        assertThat(configRow.rowInDocument).isEqualTo(1)
        assertThat(configRow.sourceColumns.map { it.title }).hasSameElementsAs(listOf("English", "Polish"))
    }

    @Test
    fun finding_project_key_column_when_project_columns_are_specified_explicitly() {
        val sheetContent = """
            ┌──────────────────────────────────────────┐
            │Android Key│iOS Key   │English  │Polish   │
            ├──────────────────────────────────────────┤
            │           │          │         │         │
            └──────────────────────────────────────────┘
        """
        val configRow = ConfigRowFinder.findConfigRowIn(mockSheet(sheetContent))!!
        val androidKeyColumn = configRow.keyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumn).isEqualTo(0)
        val iOSKeyColumn = configRow.keyColumnForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumn).isEqualTo(1)
    }

    @Test
    fun finding_key_column_when_project_column_is_specified_generally() {
        val sheetContent = """
            ┌───────────────────────────────┐
            │Identifier │English  │Polish   │
            ├───────────────────────────────┤
            │           │         │         │
            └───────────────────────────────┘
        """
        val configRow = ConfigRowFinder.findConfigRowIn(mockSheet(sheetContent))!!
        val androidKeyColumn = configRow.keyColumnForProjectType(ProjectType.Android)
        assertThat(androidKeyColumn).isEqualTo(0)
        val iOSKeyColumn = configRow.keyColumnForProjectType(ProjectType.iOS)
        assertThat(iOSKeyColumn).isEqualTo(0)
    }

    @Test
    fun throwing_exception_when_getting_column_for_not_existing_project() {
        val sheetContent = """
            ┌────────────────────────────┐
            │iOS Key │English  │Polish   │
            ├────────────────────────────┤
            │        │         │         │
            └────────────────────────────┘
        """
        assertThatExceptionOfType(ImportException::class.java)
                .isThrownBy{
                    val configRow = ConfigRowFinder.findConfigRowIn(mockSheet(sheetContent))!!
                    configRow.keyColumnForProjectType(ProjectType.Android)
                }
    }
}