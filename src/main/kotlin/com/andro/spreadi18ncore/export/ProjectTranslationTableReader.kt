package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.sourcesheet.ValueTransformations
import com.andro.spreadi18ncore.project.TranslationTable
import com.andro.spreadi18ncore.project.TranslationTableReader
import com.andro.spreadi18ncore.project.languageTags
import com.andro.spreadi18ncore.valuetransformation.CustomValueTransformation
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation

internal class ProjectTranslationTableReader(
    private val project: Project,
    private val valueTransformations: ValueTransformations?
) : TranslationTableReader {

    private val valueTransformation: ValueTransformation? by lazy {
        if (valueTransformations != null) {
            CustomValueTransformation(valueTransformations)
        } else {
            null
        }
    }

    override fun read(): TranslationTable {
        val translationTable = TranslationTable(project.languageTags)
        project.localizationFiles.forEach { localizationFile ->
            project.keyValueReader(localizationFile).use { reader ->
                var keyValue = reader.read(valueTransformation)
                while (keyValue != null) {
                    translationTable.setValue(localizationFile.languageTag, keyValue)
                    keyValue = reader.read(valueTransformation)
                }
            }
        }
        return translationTable
    }
}