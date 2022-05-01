import internal.ImportException
import internal.Importer
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path


class Import() {

    fun perform(sourceFilePath: Path, targetProjectPath: Path) {
        try {
            workbook(sourceFilePath).use {
                val importer = Importer(it.getSheetAt(0), targetProjectPath)
                importer.import()
            }
        } catch (exc: ImportException) {
            throw exc
        } catch (exc: Exception) {
            throw UnknownImportError(exc)
        }
    }

    private fun workbook(sourceFilePath: Path): XSSFWorkbook {
        try {
            val file = FileInputStream(sourceFilePath.toFile())
            return XSSFWorkbook(file)
        } catch (exc: Exception) {
            throw WorkbookOpeningError(exc)
        }
    }
}

internal class UnknownImportError(exc: Exception): ImportException(cause = exc) {}
internal class WorkbookOpeningError(exc: Exception): ImportException(cause = exc) {}
