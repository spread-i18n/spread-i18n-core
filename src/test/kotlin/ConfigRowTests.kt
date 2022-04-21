import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigRowTests {

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
    fun finds_configRow_with_expected_values() {
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
}