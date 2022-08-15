package com.andro.spreadi18ncore

import com.andro.spreadi18ncore.importing.AndroidSourceTargetMatcher
import com.andro.spreadi18ncore.importing.MatchedSourceAndTarget
import com.andro.spreadi18ncore.importing.MatchedSourcesAndTargets
import com.andro.spreadi18ncore.importing.iOSSourceTargetMatcher
import com.andro.spreadi18ncore.sourcesheet.ColumnIndex
import com.andro.spreadi18ncore.sourcesheet.LocaleCell
import com.andro.spreadi18ncore.sourcesheet.RowIndex
import com.andro.spreadi18ncore.targetproject.LocalizationDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LocaleCellsBuilder {
    val cells = mutableListOf<LocaleCell>()
    fun addLocale(localeName: String): LocaleCellsBuilder {
        val cell = LocaleCell(RowIndex(0), ColumnIndex(cells.size), localeName)
        cells.add(cell)
        return this
    }
}

class AndroidMatchingTests {

    @Test
    fun matching_by_simple_tag_without_region() {
        val sources = LocaleCellsBuilder()
            .addLocale("Polish")
            .addLocale("French")
            .addLocale("English").cells

        val targets = listOf(
            "res/values-fr".asTargetDir(),
            "res/values-pl".asTargetDir(),
            "res/values".asTargetDir()
        )

        val result = AndroidSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(3)
        assertThat(result.getMatchWithTitle("Polish").path).isEqualTo("res/values-pl")
        assertThat(result.getMatchWithTitle("French").path).isEqualTo("res/values-fr")
        assertThat(result.getMatchWithTitle("English").path).isEqualTo("res/values")
    }

    @Test
    fun matching_by_tag_with_region() {
        val sources = LocaleCellsBuilder()
            .addLocale("pl")
            .addLocale("French")
            .addLocale("en_US").cells

        val targets = listOf(
            "res/values-fr-rFR".asTargetDir(),
            "res/values-pl".asTargetDir(),
            "res/values-en-rUS".asTargetDir()
        )

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
        val sources = LocaleCellsBuilder()
            .addLocale("fr_DZ")
            .addLocale("fr_ML")
            .addLocale("fr_PM")
            .addLocale("fr_MG")
            .addLocale("english")
            .addLocale("fr_MF").cells

        val targets = listOf(
            LocalizationDirectory("project/fr-PM.lproj"),
            LocalizationDirectory("project/fr-ML.lproj"),
            LocalizationDirectory("project/fr-DZ.lproj"),
            LocalizationDirectory("project/fr-MG.lproj"),
            LocalizationDirectory("project/en.lproj"),
            LocalizationDirectory("project/fr-MF.lproj")
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
        val sources = LocaleCellsBuilder()
            .addLocale("Polish")
            .addLocale("French").cells

        val targets = listOf(
            LocalizationDirectory("project/fr-CA.lproj"),
            LocalizationDirectory("project/en.lproj")
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertEquals(1, result.count)
        assertEquals("French", result.getAt(0).sourceLocaleCell.text)
    }

    @Test
    fun matches_all_source_columns_with_target_dirs() {
        val sources = LocaleCellsBuilder()
            .addLocale("English")
            .addLocale("French").cells

        val targets = listOf(
            LocalizationDirectory("project/fr-CA.lproj"),
            LocalizationDirectory("project/en.lproj")
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertEquals(2, result.count)
    }

    @Test
    fun matches_base_directory_with_single_unmatched_column() {
        val sources = LocaleCellsBuilder()
            .addLocale("English")
            .addLocale("French").cells

        val targets = listOf(
            LocalizationDirectory("project/fr-CA.lproj"),
            LocalizationDirectory("project/Base.lproj")
        )

        val result = iOSSourceTargetMatcher()
            .match(sources, targets)

        assertThat(result.count).isEqualTo(2)
        assertThat(result.getMatchWithTitle("French").path).isEqualTo("project/fr-CA.lproj")
        assertThat(result.getMatchWithTitle("English").path).isEqualTo("project/Base.lproj")
    }
}

internal val MatchedSourceAndTarget.path: String
    get() {
        return targetDirectory.file.path
    }

internal fun MatchedSourcesAndTargets.findWithTitle(title: String): MatchedSourceAndTarget? {
    return matches.find { match -> match.sourceLocaleCell.text == title }
}

internal fun MatchedSourcesAndTargets.getMatchWithTitle(title: String): MatchedSourceAndTarget {
    return findWithTitle(title)!!
}
