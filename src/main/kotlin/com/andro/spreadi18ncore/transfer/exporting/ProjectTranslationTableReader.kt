package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.localization.LanguageTag
import com.andro.spreadi18ncore.localization.LocalizationFile
import com.andro.spreadi18ncore.transfer.translation.TranslationTable
import com.andro.spreadi18ncore.transfer.base.TranslationTableReader
import com.andro.spreadi18ncore.transfer.transformation.CustomValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformations

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
        val translationTable = TranslationTable(project.tableLanguageTags)
        project.localizationFiles.forEach { localizationFile ->
            project.keyValueReader(localizationFile).use { reader ->
                var keyValue = reader.read(valueTransformation)
                while (keyValue != null) {
                    translationTable.setValue(localizationFile.tableTag, keyValue)
                    keyValue = reader.read(valueTransformation)
                }
            }
        }
        return translationTable
    }

    private val LocalizationFile.tableTag: LanguageTag
        get() {
            return if (isDefault) {
                LanguageTag.default
            } else languageTag
        }

    private val LanguageTag.orderTag: String
        get() {
            return if (isDefault) { "a" /*make 'default' to be first */} else canonical.toLowerCase()
        }

    private val Project.tableLanguageTags: List<LanguageTag> get() =
        localizationFiles
            .map { it.tableTag }
            .sortedBy { it.orderTag }
}