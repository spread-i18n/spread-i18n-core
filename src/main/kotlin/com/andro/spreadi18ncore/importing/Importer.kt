package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.targetproject.TargetProject
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

open class ImportException(message: String? = null, cause: Throwable? = null): Exception(message, cause)

val Sheet.rows: Sequence<Row>
    get() = rowIterator().asSequence()

fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)

internal class Importer(private val sourceSheet: Sheet, private val targetProject: TargetProject) {

    fun import() {
        declaration.matchedSourcesAndTargets.forEach { match ->
            declaration.projectType.fileWriter(match.targetDirectory.path).use { fileWriter ->
                sourceSheet.rows.skipTo(declaration.firstTranslationRow).forEach { row ->
                    val keyCell = row.getCell(declaration.keyColumn)
                    val valueCell = row.getCell(match.sourceColumn.column)
                    if ((keyCell != null) && (keyCell.stringCellValue.isNotBlank()) && (valueCell != null)) {
                        fileWriter.write(key = keyCell.stringCellValue, value = valueCell.stringCellValue)
                    }
                }
            }
        }
    }

    private val evaluation: ImportEvaluation by lazy {
        ImportEvaluator().evaluate(headerRow, targetProject)
    }

    private val declaration: ImportDeclaration by lazy {
        ImportDeclaration(
            headerRow.indexOfTranslationKeyColumnForProjectType(evaluation.projectType),
            headerRow.rowWithFirstTranslation,
            evaluation.matchedSourcesAndTargets,
            evaluation.projectType
        )
    }

    private val headerRow: HeaderRow by lazy {
        HeaderRow.getFrom(sourceSheet)
    }
}
