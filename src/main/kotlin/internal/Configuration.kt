package internal

import internal.Locales.Companion.allLocales
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet

internal enum class TranslationKeyType {
    iOS {
        override val cellText: List<String>
            get() = listOf("ios")
    },
    Android {
        override val cellText: List<String>
            get() = listOf("android")
    },
    General {
        override val cellText: List<String>
            get() = listOf("key", "identifier", "id")
    };
    abstract val cellText: List<String>
}

internal class ColumnNotFound(): ImportException("")

internal data class TranslationKeyColumn(val columnIndex: Int, val translationKeyType: TranslationKeyType)

internal class TranslationKeyColumns() {
    private val translationKeyColumns = mutableSetOf<TranslationKeyColumn>()
    fun add(translationKeyColumn: TranslationKeyColumn) {
        translationKeyColumns.add(translationKeyColumn)
    }
    fun isNotEmpty() = translationKeyColumns.isNotEmpty()

    private fun findTranslationKeyColumn(translationKeyType: TranslationKeyType): TranslationKeyColumn? {
        return translationKeyColumns.find { translationKeyType == it.translationKeyType }
    }

    fun containsTranslationKeyColumnFor(translationKeyType: TranslationKeyType): Boolean {
        return findTranslationKeyColumn(translationKeyType)!=null
    }

    fun getTranslationKeyColumn(translationKeyType: TranslationKeyType): TranslationKeyColumn {
        return findTranslationKeyColumn(translationKeyType) ?: throw ColumnNotFound()
    }
}

internal class ConfigRowNotFound(): ImportException("Config row not found in the source file.")

internal data class ConfigRow(val rowInDocument: Int, val sourceColumns: Set<SourceColumn>,
                              private val translationKeyColumns: TranslationKeyColumns) {

    fun indexOfTranslationKeyColumnForProjectType(projectType: ProjectType): Int {
        if (translationKeyColumns.containsTranslationKeyColumnFor(projectType.translationKeyType)) {
            return translationKeyColumns.getTranslationKeyColumn(projectType.translationKeyType).columnIndex
        }
        return translationKeyColumns.getTranslationKeyColumn(TranslationKeyType.General).columnIndex
    }

    val rowWithFirstTranslation = rowInDocument+1

    companion object {

        fun getFrom(sheet: Sheet): ConfigRow {
            return findIn(sheet) ?: throw ConfigRowNotFound()
        }

        fun findIn(sheet: Sheet): ConfigRow? {
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
    val projectKeyColumns = TranslationKeyColumns()
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
                projectKeyColumns.add(TranslationKeyColumn(projectCellCandidate.index, translationKeyType))
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

internal data class ImportConfiguration(val keyColumn: Int,
                                        val firstTranslationRow: Int,
                                        val matchedSourcesAndTargets: MatchedSourcesAndTargets,
                                        val projectType: ProjectType)
