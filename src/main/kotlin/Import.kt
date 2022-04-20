import Locales.Companion.allLocales
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.nio.file.Path

class ConfigRowIdentifier {
    private var projectKeys = mutableSetOf<IndexedValue<ProjectKey>>()
    var locales = mutableSetOf<SourceColumn>()
    val unknownPurposeColumns = mutableListOf<IndexedValue<Cell>>()

    fun analyseCell(indexedCell: IndexedValue<Cell>) {
        if (indexedCell.value.cellType == CellType.STRING) {
            if (!storeIfLocale(indexedCell)) {
                if(!storeIfProjectKey(indexedCell)) {
                    storeUnknownPurposeColumn(indexedCell)
                }
            }
        }
    }

    private fun storeIfLocale(localeCellCandidate: IndexedValue<Cell>): Boolean {
        val locale = allLocales.findLocale(localeCellCandidate.value.stringCellValue)
        if (locale != null) {
            locales.add(SourceColumn(localeCellCandidate.value.stringCellValue, localeCellCandidate.index))
            return true
        }
        return false
    }

    private fun storeIfProjectKey(projectCellCandidate: IndexedValue<Cell>): Boolean {
        val tokens = projectCellCandidate.value.stringCellValue.split(" ").map { it.toLowerCase() }
        for (project in ProjectKey.values()) {
            val id = project.identifiers.find { tokens.contains(it) }
            if (id != null) {
                projectKeys.add(IndexedCellValue(projectCellCandidate.index, project))
                return true
            }
        }
        return false
    }

    private fun storeUnknownPurposeColumn(indexedCell: IndexedValue<Cell>) {
        unknownPurposeColumns.add(indexedCell)
    }

    val isConfigRow: Boolean
        get() {
            return projectKeys.isNotEmpty() && locales.isNotEmpty()
        }
}

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
        ImportConfiguration(configRow.keyColumn,
                configRow.rowWithFirstTranslation,
                evaluation.matchedSourcesAndTargets,
                evaluation.projectType)
    }

    private val configRow: ConfigRow by lazy {
        fun findConfigRow(): ConfigRow? {
            sheet.rowIterator().withIndex().forEach { indexedRow ->
                val configRowIdentifier = ConfigRowIdentifier()
                indexedRow.value.cellIterator().withIndex().forEach { indexedCell ->
                    configRowIdentifier.analyseCell(indexedCell)
                    println("${indexedCell.value.cellType}: ${indexedCell.value.stringCellValue}")
                }
                if (configRowIdentifier.isConfigRow) {
                    return ConfigRow(indexedRow.index, configRowIdentifier.locales, 0)
                }
            }
            return null
        }
        findConfigRow() ?: throw ImportException("Given file does not contain config row")
    }

    fun perform() {
        ImportManager(sheet).import(configuration)
    }
}

class ImportManager (private val sheet: Sheet) {

    fun import(config: ImportConfiguration) {
        config.matchedSourcesAndTargets.forEach { importAddress ->
            val fileWriter = config.projectType.fileWriter(importAddress.targetDirectory.file.toPath())
            sheet.rowIterator().asSequence().skipTo(config.firstTranslationRow).forEach {
                val keyCell = it.getCell(config.keyColumn)
                val valueCell = it.getCell(importAddress.sourceColumn.column)
                if ((keyCell!=null) && (valueCell!=null)) {
                    fileWriter.write(key = keyCell.stringCellValue, value = valueCell.stringCellValue)
                } else {
//                    add warning that file contains null cells
                }
            }
            fileWriter.close()
        }
    }
}

