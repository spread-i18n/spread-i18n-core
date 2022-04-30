import Locales.Companion.allLocales
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet

enum class TranslationKeyType {
    iOS {
        override val cellText: List<String>
            get() = listOf("ios")
    },
    Android {
        override val cellText: List<String>
            get() = listOf("android")
    },
    general {
        override val cellText: List<String>
            get() = listOf("key", "identifier", "id")
    };
    abstract val cellText: List<String>
}

class ColumnKeyNotFound(): ImportException("")

data class ProjectKeyColumn(val column: Int, val translationKeyType: TranslationKeyType)
class ProjectKeyColumns() {
    private val projectKeyColumns = mutableSetOf<ProjectKeyColumn>()
    fun add(projectKeyColumn: ProjectKeyColumn) {
        projectKeyColumns.add(projectKeyColumn)
    }
    fun isNotEmpty() = projectKeyColumns.isNotEmpty()

    private fun findColumn(translationKeyType: TranslationKeyType): ProjectKeyColumn? {
        return projectKeyColumns.find { translationKeyType == it.translationKeyType }
    }

    fun containsColumnFor(translationKeyType: TranslationKeyType): Boolean {
        return findColumn(translationKeyType)!=null
    }

    fun getColumn(translationKeyType: TranslationKeyType): Int {
        return findColumn(translationKeyType)?.let { it.column } ?: throw ColumnKeyNotFound()
    }
}

data class ConfigRow(val rowInDocument: Int, val sourceColumns: Set<SourceColumn>,
                     private val projectKeyColumns: ProjectKeyColumns) {

    fun keyColumnForProjectType(projectType: ProjectType): Int {
        if (projectKeyColumns.containsColumnFor(projectType.translationKeyType)) {
            return projectKeyColumns.getColumn(projectType.translationKeyType)
        }
        return projectKeyColumns.getColumn(TranslationKeyType.general)
    }

    val rowWithFirstTranslation = rowInDocument+1
}

internal class ConfigRowFinder {

    companion object {

        fun findConfigRowIn(sheet: Sheet): ConfigRow? {
            sheet.rowIterator().withIndex().forEach { indexedRow ->
                val configRowIdentifier = ConfigRowIdentifier()
                indexedRow.value.cellIterator().withIndex().forEach { indexedCell ->
                    configRowIdentifier.analyseCell(indexedCell)
                }
                if (configRowIdentifier.isConfigRow) {
                    return ConfigRow(indexedRow.index, configRowIdentifier.locales, configRowIdentifier.projectKeyColumns)
                }
            }
            return null
        }
    }
}

internal class ConfigRowIdentifier {
    val projectKeyColumns = ProjectKeyColumns()
    val locales = mutableSetOf<SourceColumn>()
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
        val tokens = projectCellCandidate.value.stringCellValue.trim()
                .split(" ").filter { it.isNotBlank() }.map { it.toLowerCase() }
        if (tokens.isEmpty()) {
            return false
        }
        for (translationKeyType in TranslationKeyType.values()) {
            val id = translationKeyType.cellText.find { tokens.contains(it) }
            if (id != null) {
                projectKeyColumns.add(ProjectKeyColumn(projectCellCandidate.index, translationKeyType))
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
            return projectKeyColumns.isNotEmpty() && locales.isNotEmpty()
        }
}