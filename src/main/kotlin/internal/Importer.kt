package internal

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

val Sheet.rows: Sequence<Row>
    get() = rowIterator().asSequence()

internal class Importer(private val sourceSheet: Sheet, private val targetProject: TargetProject) {

    fun import() {
        config.matchedSourcesAndTargets.forEach { match ->
            config.projectType.fileWriter(match.targetDirectory.path).use { fileWriter ->
                sourceSheet.rows.skipTo(config.firstTranslationRow).forEach { row ->
                    val keyCell = row.getCell(config.keyColumn)
                    val valueCell = row.getCell(match.sourceColumn.column)
                    if ((keyCell != null) && (valueCell != null)) {
                        fileWriter.write(key = keyCell.stringCellValue, value = valueCell.stringCellValue)
                    }
                }
            }
        }
    }

    private val evaluation: ImportEvaluation by lazy {
        ImportEvaluator().evaluate(configRow, targetProject)
    }

    private val config: ImportConfiguration by lazy {
        ImportConfiguration(configRow.indexOfTranslationKeyColumnForProjectType(evaluation.projectType),
                configRow.rowWithFirstTranslation,
                evaluation.matchedSourcesAndTargets,
                evaluation.projectType)
    }

    private val configRow: ConfigRow by lazy {
        ConfigRow.getFrom(sourceSheet)
    }
}
