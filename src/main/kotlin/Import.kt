import internal.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


class Import(private val sourceFilePath: Path, private val targetProjectPath: Path) {

    private val sheet: Sheet by lazy(LazyThreadSafetyMode.NONE) {
        val file = FileInputStream(sourceFilePath.toFile())
        val workbook = XSSFWorkbook(file)
        workbook.getSheetAt(0)
    }

    private val evaluation: ImportEvaluation by lazy {
        ImportEvaluator().evaluate(configRow, targetProjectPath.toFile())
    }

    private val configuration: ImportConfiguration by lazy {
        ImportConfiguration(configRow.indexOfTranslationKeyColumnForProjectType(evaluation.projectType),
                configRow.rowWithFirstTranslation,
                evaluation.matchedSourcesAndTargets,
                evaluation.projectType)
    }

    private val configRow: ConfigRow by lazy {
        ConfigRow.getFrom(sheet)
    }

    fun perform() {
        try {
            Importer.import(sheet, configuration)
        } catch (exc: ImportException) {
            throw exc
        } catch (exc: Exception) {
            throw UnknownImportError(exc)
        }
    }
}

class UnknownImportError(exc: Exception): ImportException(cause = exc) {}