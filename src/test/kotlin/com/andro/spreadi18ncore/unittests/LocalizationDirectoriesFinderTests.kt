package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.helpers.dir
import com.andro.spreadi18ncore.project.AndroidLocalizationFileFinder
import com.andro.spreadi18ncore.project.iOSLocalizationFileFinder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalizationDirectoriesFinderTests {

    @Test
    fun `Finding localization directories in iOS project`() {
        val rootDir = dir("ProjectRoot") {
            dir("Assets.xcassets") {
                dir("AppIcon.appiconset") { file("Contents.json") }
            }
            dir("Base.lproj") { file("Localizable.strings") }
            dir("Resources") {
                dir("fr.lproj") { file("Localizable.strings") }
            }
        }
        val res = iOSLocalizationFileFinder().findLocalizationsFileIn(rootDir)
        assertThat(res.map { it.path.toString() }).hasSameElementsAs(listOf("Base.lproj", "fr.lproj"))
    }

    @Test
    fun `Finding localization directories in Android project`() {
        val rootDir = dir("app") {
            dir("src") {
                dir("main") {
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
        val res = AndroidLocalizationFileFinder().findLocalizationsFileIn(rootDir)
        assertThat(res.map { it.path.toString() }).hasSameElementsAs(listOf("values", "values-pl"))
    }
}

