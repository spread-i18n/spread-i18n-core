package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.excel.ValueTransformations
import com.andro.spreadi18ncore.transfer.translation.TranslationTable
import com.andro.spreadi18ncore.transfer.base.TranslationTableReader
import com.andro.spreadi18ncore.transfer.translation.languageTags
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation

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