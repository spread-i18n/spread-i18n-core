import internal.AndroidLocalizationDirectoriesFinder
import internal.iOSLocalizationDirectoriesFinder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalizationDirectoriesFinderTests {

    @Test
    fun `finding localization directories in iOS project`() {
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

    @Test
    fun `finding localization directories in Android project`() {
        val rootDir = dir("app") {
            dir("src") {
                dir("main"){
                    dir("java") {
                    }
                    dir("res") {
                        dir("anim") {
                            file("fadein.xml")
                            file("fadeout.xml")
                        }
                        dir("values") {//<<<<<<<<
                            file("styles.xml")
                            file("strings.xml")
                        }
                        dir("values-land") {
                            file("dimens.xml")
                        }
                        dir("values-pl") {//<<<<<<<<
                            file("strings.xml")
                        }
                    }
                }
            }
        }
        val res = AndroidLocalizationDirectoriesFinder().findLocalizationDirectoriesIn(rootDir)
        assertThat(res.map { it.path.toString() }).hasSameElementsAs(listOf("values", "values-pl"))
    }
}

