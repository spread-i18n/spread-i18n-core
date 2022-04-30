import io.mockk.every
import io.mockk.mockk
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

fun mockSheet(sheetContent: String): Sheet {
    return mockk{
        every { rowIterator() } returns
                sheetContent.lineSequence()
                        .map { it.trim() }
                        .filter { it.startsWith("│") }
                        .map {
                            val row = mockk<Row>()
                            every { row.cellIterator() } returns it.split("│").asSequence().skipTo(1).map { cellContent ->
                                val cell = mockk<Cell>()
                                every { cell.stringCellValue } returns cellContent
                                every { cell.cellType } returns CellType.STRING
                                cell
                            }.iterator()
                            row
                        }
                        .iterator()
    }
}