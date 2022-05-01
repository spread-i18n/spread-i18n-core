package internal

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

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
