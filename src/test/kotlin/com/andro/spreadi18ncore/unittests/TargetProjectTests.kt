package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.helpers.dir
import com.andro.spreadi18ncore.helpers.mockkedPath
import com.andro.spreadi18ncore.targetproject.AndroidManifest
import com.andro.spreadi18ncore.targetproject.xcodeprojDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TargetProjectTests {

    @Test
    fun `xcodeproj is discovered in an iOS project structure`() {
        val iOSProjRootDir = dir("ProjectA") {
            dir("ProjectA.xcodeproj") {}
        }
        assertThat(xcodeprojDirectory.existsIn(iOSProjRootDir.mockkedPath)).isTrue
    }

    @Test
    fun `AndroidManifest is discovered in an Android project structure`() {
        val androidProjRootDir = dir("app") {
            dir("src") {
                dir("main") {
                    dir("java") {
                    }
                    file("AndroidManifest.xml")
                }
            }
        }
        assertThat(AndroidManifest.existsIn(androidProjRootDir.mockkedPath)).isTrue
    }

    @Test
    fun `AndroidManifest is not discovered in incomplete Android project structure`() {
        val androidProjRootDir = dir("app") {
            dir("src") {
                dir("main") {
                    dir("java") {
                    }
                }
            }
        }
        assertThat(AndroidManifest.existsIn(androidProjRootDir.mockkedPath)).isFalse
    }
}