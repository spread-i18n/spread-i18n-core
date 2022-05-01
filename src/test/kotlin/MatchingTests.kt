import internal.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class MatchingTests {

    @Test
    fun matches_by_tag_translations_existing_in_source_and_target() {
        val sources = listOf(
                SourceColumn("fr_DZ", 0),
                SourceColumn("fr_ML", 1),
                SourceColumn("fr_PM", 2),
                SourceColumn("fr_MG", 3),
                SourceColumn("english", 4),
                SourceColumn("fr_MF", 5))
        val targets = listOf(
                TargetDirectory(File("project/fr-PM.lproj")),
                TargetDirectory(File("project/fr-ML.lproj")),
                TargetDirectory(File("project/fr-DZ.lproj")),
                TargetDirectory(File("project/fr-MG.lproj")),
                TargetDirectory(File("project/en.lproj")),
                TargetDirectory(File("project/fr-MF.lproj")))

        val result = iOSSourceTargetMatcher().match(sources, targets)

        assertThat(result.count).isEqualTo(6)
        assertThat(result.getWithTitle("fr_DZ").path).isEqualTo("project/fr-DZ.lproj")
        assertThat(result.getWithTitle("fr_ML").path).isEqualTo("project/fr-ML.lproj")
        assertThat(result.getWithTitle("fr_PM").path).isEqualTo("project/fr-PM.lproj")
        assertThat(result.getWithTitle("fr_MG").path).isEqualTo("project/fr-MG.lproj")
        assertThat(result.getWithTitle("english").path).isEqualTo("project/en.lproj")
        assertThat(result.getWithTitle("fr_MF").path).isEqualTo("project/fr-MF.lproj")
    }

    @Test
    fun matches_only_translations_existing_in_source_and_target() {
        val sources = listOf(
                SourceColumn("Polish", 0),
                SourceColumn("French", 1))
        val targets = listOf(
                TargetDirectory(File("project/fr-CA.lproj")),
                TargetDirectory(File("project/en.lproj")))

        val result = iOSSourceTargetMatcher().match(sources, targets)

        assertEquals(1, result.count)
        assertEquals("French", result.getAt(0).sourceColumn.title)
    }

    @Test
    fun matches_all_source_columns_with_target_dirs() {
        val sources = listOf(
                SourceColumn("English", 0),
                SourceColumn("French", 1))
        val targets = listOf(
                TargetDirectory(File("project/fr-CA.lproj")),
                TargetDirectory(File("project/en.lproj")))

        val result = iOSSourceTargetMatcher().match(sources, targets)

        assertEquals(2, result.count)
    }

    @Test
    fun matches_base_directory_with_single_unmatched_column() {
        val sources = listOf(
                SourceColumn("English", 0),
                SourceColumn("French", 1))
        val targets = listOf(
                TargetDirectory(File("project/fr-CA.lproj")),
                TargetDirectory(File("project/Base.lproj")))

        val result = iOSSourceTargetMatcher().match(sources, targets)

        assertThat(result.count).isEqualTo(2)
        assertThat(result.getWithTitle("French").path).isEqualTo("project/fr-CA.lproj")
        assertThat(result.getWithTitle("English").path).isEqualTo("project/Base.lproj")
    }
}

internal val MatchedSourceAndTarget.path: String
    get() { return targetDirectory.file.path }

internal fun MatchedSourcesAndTargets.findWithTitle(title: String): MatchedSourceAndTarget? {
    return matches.find { match -> match.sourceColumn.title == title }
}

internal fun MatchedSourcesAndTargets.getWithTitle(title: String): MatchedSourceAndTarget {
    return findWithTitle(title)!!
}
