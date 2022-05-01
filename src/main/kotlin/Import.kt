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
