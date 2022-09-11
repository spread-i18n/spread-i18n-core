package com.andro.spreadi18ncore.transfer.translation

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.project.ProjectType
import com.andro.spreadi18ncore.transfer.base.TranslationTableWriter
import com.andro.spreadi18ncore.transfer.exporting.ExcelTranslationTableWriter
import com.andro.spreadi18ncore.transfer.importing.ProjectTranslationTableWriter
import java.nio.file.Path

internal interface TranslationsDestination {
    val translationTableWriter: TranslationTableWriter
}

internal class ProjectTranslationsDestination(private val project: Project) : TranslationsDestination {

    override val translationTableWriter: TranslationTableWriter
        get() = ProjectTranslationTableWriter(project)
}

internal class ExcelFileDestination(
    private val filePath: Path,
    private val type: ProjectType
) : TranslationsDestination {

    override val translationTableWriter: TranslationTableWriter
        get() = ExcelTranslationTableWriter(filePath)
}
