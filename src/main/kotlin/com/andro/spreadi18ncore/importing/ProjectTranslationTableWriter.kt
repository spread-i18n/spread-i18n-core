package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.targetproject.*
import com.andro.spreadi18ncore.targetproject.LocalizationFile
import com.andro.spreadi18ncore.targetproject.TranslationTable
import com.andro.spreadi18ncore.targetproject.TranslationTableWriter

internal class ProjectTranslationTableWriter(private val project: Project) : TranslationTableWriter {

    override fun write(translationTable: TranslationTable) {
        translationTable.languageTags.forEach { languageTag ->
            project.findLocalizationFileFor(languageTag)?.let { localizationFile ->
                project.keyValueWriter(localizationFile).use { writer ->
                    translationTable.keyValues(languageTag).forEach { keyValue ->
                        writer.write(keyValue)
                    }
                }
            }
        }
    }

    private fun Project.findLocalizationFileFor(languageTag: LanguageTag): LocalizationFile? =
        localizationFiles.firstOrNull { it.languageTag == languageTag }
}