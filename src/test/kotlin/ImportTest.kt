import org.junit.jupiter.api.Test
import java.nio.file.Path

class ImportTest {

    @Test
    fun performs_import() {
        val sourceFilePath = Path.of("sample.xlsx")
        val targetProjectPath = Path.of("/Users/zebul/Projects/sandbox/iOS/AProject")
        val import = Import(sourceFilePath = sourceFilePath, targetProjectPath = targetProjectPath)
        import.perform()
    }

}