package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.sourcesheet.ColumnIndex
import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.valuetransformation.CustomValueTransformation
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

open class ImportException(message: String? = null, cause: Throwable? = null): Exception(message, cause)

val Sheet.rows: Sequence<Row>
    get() = rowIterator().asSequence()

fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)

fun Row.getCell(columnIndex: ColumnIndex): Cell? = getCell(columnIndex.value)

typealias ValueTransformations = Map<String, String>
internal class Importer(private val sheet: Sheet,
                        private val project: Project,
                        private val valueTransformations: ValueTransformations? = null) {

    private val valueTransformation: ValueTransformation by lazy {
        valueTransformations?.let { CustomValueTransformation(valueTransformations) }
                ?: declaration.projectType.valueTransformation
    }

    fun import() {
        declaration.matchedSourcesAndTargets.forEach { match ->
            declaration.projectType.fileWriter(match.targetDirectory.path).use { fileWriter ->
                sheet.rows.skipTo(declaration.firstTranslationRow).forEach { row ->
                    val keyCell = row.getCell(declaration.keyColumnIndex)
                    val valueCell = row.getCell(match.sourceLocaleCell.columnIndex)
                    if ((keyCell != null) && (keyCell.stringCellValue.isNotBlank()) && (valueCell != null)) {
                        val value = valueTransformation.transform(valueCell.stringCellValue)
                        fileWriter.write(key = keyCell.stringCellValue, value = value)
                    }
                }
            }
        }
    }

    private val evaluation: ImportEvaluation by lazy {
        ImportEvaluator().evaluate(headerRow, project)
    }

    private val declaration: ImportDeclaration by lazy {
        ImportDeclaration(
            headerRow.columnIndexForProjectType(evaluation.projectType),
            headerRow.rowWithFirstTranslation,
            evaluation.matchedSourcesAndTargets,
            evaluation.projectType
        )
    }

    private val headerRow: HeaderRow by lazy {
        HeaderRow.getFrom(sheet)
    }
}
