import internal.iOSLocalizationDirectoriesFinder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalizationDirectoriesFinderTests {

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
        val res = iOSLocalizationDirectoriesFinder().findLocalizationDirectoriesIn(rootDir)
        assertThat(res.map { it.path.toString() }).hasSameElementsAs(listOf("Base.lproj", "fr.lproj"))
    }
}

