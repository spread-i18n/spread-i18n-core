package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.targetproject.TranslationTable
import com.andro.spreadi18ncore.targetproject.TranslationTableReader
import com.andro.spreadi18ncore.targetproject.languageTags

internal class ProjectTranslationTableReader(private val project: Project) : TranslationTableReader {

    override fun read(): TranslationTable {
        val translationTable = TranslationTable(project.languageTags)
        project.localizationFiles.forEach { localizationFile ->
            project.keyValueReader(localizationFile).use { reader ->
                var keyValue = reader.read()
                while (keyValue != null) {
                    translationTable.setValue(localizationFile.languageTag, keyValue)
                    keyValue = reader.read()
                }
            }
        }
        return translationTable
    }
}