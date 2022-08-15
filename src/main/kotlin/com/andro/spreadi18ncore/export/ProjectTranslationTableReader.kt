package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.targetproject.TranslationTable
import com.andro.spreadi18ncore.targetproject.TranslationTableReader
import com.andro.spreadi18ncore.targetproject.extractLocaleValue
import com.andro.spreadi18ncore.targetproject.locales

internal class ProjectTranslationTableReader(private val project: Project) : TranslationTableReader {

    override fun read(): TranslationTable {
        val translationTable = TranslationTable(project.locales)
        project.localizationDirectories.forEach { localizationDirectory ->
            project.translationFileReader(localizationDirectory).use { reader ->
                var keyValue = reader.read()
                while (keyValue != null) {
                    val localeValue = project.extractLocaleValue(localizationDirectory)
                    translationTable.setValue(localeValue, keyValue)
                    keyValue = reader.read()
                }
            }
        }
        return translationTable
    }
}