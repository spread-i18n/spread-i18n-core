import internal.*
import internal.ConfigRow
import internal.ConfigRowFinder
import internal.ImportConfiguration
import internal.ImportEvaluation
import internal.ImportEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


class Import(private val sourceFilePath: Path, private val targetProjectPath: Path) {

    private val sheet: Sheet by lazy {
        val file = FileInputStream(sourceFilePath.toFile())
        val workbook = XSSFWorkbook(file)
        workbook.getSheetAt(0)
    }

    private val evaluation: ImportEvaluation by lazy {
        ImportEvaluator().evaluate(configRow, targetProjectPath.toFile())
    }

    private val configuration: ImportConfiguration by lazy {
        ImportConfiguration(configRow.keyColumnForProjectType(evaluation.projectType),
                configRow.rowWithFirstTranslation,
                evaluation.matchedSourcesAndTargets,
                evaluation.projectType)
    }

    private val configRow: ConfigRow by lazy {
        ConfigRowFinder.findConfigRowIn(sheet)
                ?: throw ImportException("Given file does not contain config row")
    }

    fun perform() {
        Importer.import(sheet, configuration)
    }
}

val Sheet.rows: Sequence<Row>
    get() = rowIterator().asSequence()

internal class Importer {

    private constructor()

    companion object {
        fun import(sheet: Sheet, config: ImportConfiguration) {
            config.matchedSourcesAndTargets.forEach { match ->
                config.projectType.fileWriter(match.targetDirectory.path).use { fileWriter ->
                    sheet.rows.skipTo(config.firstTranslationRow).forEach { row ->
                        val keyCell = row.getCell(config.keyColumn)
                        val valueCell = row.getCell(match.sourceColumn.column)
                        if ((keyCell != null) && (valueCell != null)) {
                            fileWriter.write(key = keyCell.stringCellValue, value = valueCell.stringCellValue)
                        }
                    }
                }
            }
        }
    }
}
