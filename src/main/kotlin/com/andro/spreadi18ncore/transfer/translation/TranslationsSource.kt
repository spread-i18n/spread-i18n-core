package com.andro.spreadi18ncore.transfer.translation

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.excel.ValueTransformations
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.transfer.base.TranslationTableReader
import com.andro.spreadi18ncore.transfer.base.TranslationTableWriter
import com.andro.spreadi18ncore.transfer.exporting.ExcelTranslationTableWriter
import com.andro.spreadi18ncore.transfer.exporting.ProjectTranslationTableReader
import com.andro.spreadi18ncore.transfer.importing.ExcelTranslationTableReader
import com.andro.spreadi18ncore.transfer.importing.ProjectTranslationTableWriter
import java.nio.file.Path

internal interface TranslationsSource {
    val translationTableReader: TranslationTableReader
}

internal class ProjectTranslationsSource(
    private val project: Project,
    private val valueTransformations: ValueTransformations? = null
) : TranslationsSource {

    override val translationTableReader: TranslationTableReader
        get() = ProjectTranslationTableReader(project, valueTransformations)
}

internal class ExcelFileSource(
    private val filePath: Path,
    private val type: ProjectType,
    private val valueTransformations: ValueTransformations? = null
) : TranslationsSource {

    override val translationTableReader: TranslationTableReader
        get() = ExcelTranslationTableReader(filePath, type, valueTransformations)
}
