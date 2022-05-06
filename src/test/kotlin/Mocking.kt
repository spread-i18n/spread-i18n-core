import internal.TargetDirectory
import internal.dirs
import internal.files
import internal.skipTo
import io.mockk.MockKSettings.relaxed
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.nio.file.Path

fun mockSheet(sheetContent: String): Sheet {

    fun mockCells(rowContent: String):List<Cell> {
        return rowContent.split("│").asSequence().skipTo(1).map { cellContent ->
            val cell = mockk<Cell>()
            every { cell.stringCellValue } returns cellContent.trim()
            every { cell.cellType } returns CellType.STRING
            cell
        }.toList()
    }

    return mockk{
        every { rowIterator() } answers {
            sheetContent.lineSequence()
                    .map { it.trim() }
                    .filter { it.startsWith("│") }
                    .map { rowContent ->
                        println("Row:$rowContent")
                        val row = mockk<Row>()
                        every { row.cellIterator() } returns mockCells(rowContent).iterator()

                        val index = slot<Int>()
                        every { row.getCell(capture(index)) } answers {
                            mockCells(rowContent)[index.captured]
                        }
                        row
                    }
                    .iterator()
        }
    }
}

class Dir(dirBlock: Dir.()->Unit) {
    private val files = mutableListOf<File>()
    private val dirs = mutableListOf<File>()

    init {
        dirBlock()
    }

    fun file(name: String): File {
        val file = mockk<File>()
        every { file.name } returns name
        every { file.toPath() } returns Path.of(name)
        every { file.isDirectory } returns false
        files.add(file)
        return file
    }

    fun dir(name: String, dirBlock: Dir.()->Unit): File {
        val dir = mockk<File>()
        every { dir.name } returns name
        every { dir.toPath() } returns Path.of(name)
        every { dir.isDirectory } returns true
        every { dir.dirs } returns Dir(dirBlock).allDirs()
        every { dir.files } returns Dir(dirBlock).allFiles()
        dirs.add(dir)
        return dir
    }

    private fun allDirs() = dirs.toTypedArray()
    private fun allFiles() = files.toTypedArray()
}

fun dir(name: String, block: Dir.()->Unit): File = Dir(){}.dir(name, block)

internal fun String.asTargetDir() = TargetDirectory(File(this))
