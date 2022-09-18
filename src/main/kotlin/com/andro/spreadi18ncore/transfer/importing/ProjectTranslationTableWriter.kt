package com.andro.spreadi18ncore.transfer.importing

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.localization.LanguageTag
import com.andro.spreadi18ncore.localization.LocalizationFile
import com.andro.spreadi18ncore.transfer.translation.TranslationTable
import com.andro.spreadi18ncore.transfer.base.TranslationTableWriter

internal class ProjectTranslationTableWriter(private val project: Project) : TranslationTableWriter {

    override fun write(translationTable: TranslationTable) {
        translationTable.languageTags.forEach { languageTag ->
            project.findLocalizationFileIdentifiedBy(languageTag)?.let { localizationFile ->
                project.keyValueWriter(localizationFile).use { writer ->
                    translationTable.keyValues(languageTag).forEach { keyValue ->
                        writer.write(keyValue)
                    }
                }
            }
        }
    }

    private fun Project.findLocalizationFileIdentifiedBy(languageTag: LanguageTag): LocalizationFile? =
        localizationFiles.firstOrNull { it.containsTranslationIdentifiedBy(languageTag) }
}