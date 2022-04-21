import Locales.Companion.allLocales
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

data class ConfigRow(val rowInDocument: Int, val sourceColumns: Set<SourceColumn>, val keyColumn: Int) {
    val rowWithFirstTranslation = rowInDocument+1
}

class ConfigRowFinder {

    companion object {

        fun findConfigRowIn(sheet: Sheet): ConfigRow? {
            sheet.rowIterator().withIndex().forEach { indexedRow ->
                val configRowIdentifier = ConfigRowIdentifier()
                indexedRow.value.cellIterator().withIndex().forEach { indexedCell ->
                    configRowIdentifier.analyseCell(indexedCell)
                }
                if (configRowIdentifier.isConfigRow) {
                    return ConfigRow(indexedRow.index, configRowIdentifier.locales, 0)
                }
            }
            return null
        }
    }
}

class ConfigRowIdentifier {
    private var projectKeys = mutableSetOf<IndexedValue<ProjectKey>>()
    var locales = mutableSetOf<SourceColumn>()
    val unknownPurposeColumns = mutableListOf<IndexedValue<Cell>>()

    fun analyseCell(indexedCell: IndexedValue<Cell>) {
        if (indexedCell.value.cellType == CellType.STRING) {
            listOf(::storeIfLocale, ::storeIfProjectKey, ::storeUnknownPurposeColumn).forEach { store ->
                if( store(indexedCell) ) {
                    return
                }
            }
        }
    }

    private fun storeIfLocale(localeCellCandidate: IndexedValue<Cell>): Boolean {
        val localeCandidate = localeCellCandidate.value.stringCellValue.trim()
        if (localeCandidate.isEmpty()) {
            return false
        }
        val locale = allLocales.findLocale(localeCandidate)
        if (locale != null) {
            locales.add(SourceColumn(localeCandidate, localeCellCandidate.index))
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

    private fun storeUnknownPurposeColumn(indexedCell: IndexedValue<Cell>): Boolean {
        unknownPurposeColumns.add(indexedCell)
        return true
    }

    val isConfigRow: Boolean
        get() {
            return projectKeys.isNotEmpty() && locales.isNotEmpty()
        }
}