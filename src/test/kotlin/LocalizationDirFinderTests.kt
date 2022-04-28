import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

class Dir(dirBlock: Dir.()->Unit) {
    private val files = mutableListOf<File>()
    private val dirs = mutableListOf<File>()

    init {
        dirBlock()
    }

    fun file(name: String): File{
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
        dirs.add(dir)
        return dir
    }

    private fun allDirs() = dirs.toTypedArray()
}

fun dir(name: String, block: Dir.()->Unit): File = Dir(){}.dir(name, block)


class LocalizationDirFinderTests {

    @Test
    fun finds_localization_directories_of_iOS_project() {
        val rootDir = dir("ProjectRoot") {
            dir("Assets.xcassets") {
                dir("AppIcon.appiconset"){ file("Contents.json") }
            }
            dir("Base.lproj") { file("Localizable.strings") }
            dir("Resources") {
                dir("fr.lproj") { file("Localizable.strings") }
            }
        }
        val res = iOSLocalizationDirFinder().findLocalizationDirsIn(rootDir)
        assertThat(res.map { it.path.toString() }).hasSameElementsAs(listOf("Base.lproj", "fr.lproj"))
    }
}

