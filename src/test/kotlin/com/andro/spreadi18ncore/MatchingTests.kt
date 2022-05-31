package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.sourcetargetmatching.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class AndroidMatchingTests {

    @Test
    fun matching_by_simple_tag_without_region() {
        val sources = listOf(
            SourceColumn("Polish", 0),
            SourceColumn("French", 1),
            SourceColumn("English", 2)
        )
        val targets = listOf(
                "res/values-fr".asTargetDir(),
                "res/values-pl".asTargetDir(),
                "res/values".asTargetDir())

        val result = AndroidSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(3)
        assertThat(result.getMatchWithTitle("Polish").path).isEqualTo("res/values-pl")
        assertThat(result.getMatchWithTitle("French").path).isEqualTo("res/values-fr")
        assertThat(result.getMatchWithTitle("English").path).isEqualTo("res/values")
    }

    @Test
    fun matching_by_tag_with_region() {
        val sources = listOf(
            SourceColumn("pl", 0),
            SourceColumn("French", 1),
            SourceColumn("en_US", 2)
        )
        val targets = listOf(
                "res/values-fr-rFR".asTargetDir(),
                "res/values-pl".asTargetDir(),
                "res/values-en-rUS".asTargetDir())

        val result = AndroidSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(3)
        assertThat(result.getMatchWithTitle("pl").path).isEqualTo("res/values-pl")
        assertThat(result.getMatchWithTitle("French").path).isEqualTo("res/values-fr-rFR")
        assertThat(result.getMatchWithTitle("en_US").path).isEqualTo("res/values-en-rUS")
    }
}

@Suppress("ClassName")
class iOSMatchingTests {

    @Test
    fun matches_by_tag_translations_existing_in_source_and_target() {
        val sources = listOf(
            SourceColumn("fr_DZ", 0),
            SourceColumn("fr_ML", 1),
            SourceColumn("fr_PM", 2),
            SourceColumn("fr_MG", 3),
            SourceColumn("english", 4),
            SourceColumn("fr_MF", 5)
        )
        val targets = listOf(
            TargetDirectory(File("project/fr-PM.lproj")),
            TargetDirectory(File("project/fr-ML.lproj")),
            TargetDirectory(File("project/fr-DZ.lproj")),
            TargetDirectory(File("project/fr-MG.lproj")),
            TargetDirectory(File("project/en.lproj")),
            TargetDirectory(File("project/fr-MF.lproj"))
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(6)
        assertThat(result.getMatchWithTitle("fr_DZ").path).isEqualTo("project/fr-DZ.lproj")
        assertThat(result.getMatchWithTitle("fr_ML").path).isEqualTo("project/fr-ML.lproj")
        assertThat(result.getMatchWithTitle("fr_PM").path).isEqualTo("project/fr-PM.lproj")
        assertThat(result.getMatchWithTitle("fr_MG").path).isEqualTo("project/fr-MG.lproj")
        assertThat(result.getMatchWithTitle("english").path).isEqualTo("project/en.lproj")
        assertThat(result.getMatchWithTitle("fr_MF").path).isEqualTo("project/fr-MF.lproj")
    }

    @Test
    fun matches_only_translations_existing_in_source_and_target() {
        val sources = listOf(
            SourceColumn("Polish", 0),
            SourceColumn("French", 1)
        )
        val targets = listOf(
            TargetDirectory(File("project/fr-CA.lproj")),
            TargetDirectory(File("project/en.lproj"))
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertEquals(1, result.count)
        assertEquals("French", result.getAt(0).sourceColumn.text)
    }

    @Test
    fun matches_all_source_columns_with_target_dirs() {
        val sources = listOf(
            SourceColumn("English", 0),
            SourceColumn("French", 1)
        )
        val targets = listOf(
            TargetDirectory(File("project/fr-CA.lproj")),
            TargetDirectory(File("project/en.lproj"))
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertEquals(2, result.count)
    }

    @Test
    fun matches_base_directory_with_single_unmatched_column() {
        val sources = listOf(
            SourceColumn("English", 0),
            SourceColumn("French", 1)
        )
        val targets = listOf(
            TargetDirectory(File("project/fr-CA.lproj")),
            TargetDirectory(File("project/Base.lproj"))
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(2)
        assertThat(result.getMatchWithTitle("French").path).isEqualTo("project/fr-CA.lproj")
        assertThat(result.getMatchWithTitle("English").path).isEqualTo("project/Base.lproj")
    }
}

internal val MatchedSourceAndTarget.path: String
    get() { return targetDirectory.file.path }

internal fun MatchedSourcesAndTargets.findWithTitle(title: String): MatchedSourceAndTarget? {
    return matches.find { match -> match.sourceColumn.text == title }
}

internal fun MatchedSourcesAndTargets.getMatchWithTitle(title: String): MatchedSourceAndTarget {
    return findWithTitle(title)!!
}
