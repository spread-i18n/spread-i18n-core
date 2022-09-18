package com.andro.spreadi18ncore.unittests

import com.andro.spreadi18ncore.helpers.dir
import com.andro.spreadi18ncore.helpers.mockkedPath
import com.andro.spreadi18ncore.project.AndroidManifest
import com.andro.spreadi18ncore.project.pbxprojFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectTests {

    @Test
    fun `Project pbxproj file is discovered in an iOS project structure`() {
        val iOSProjRootDir = dir("ProjectA") {
            dir("ProjectA.xcodeproj") {
                file("project.pbxproj")
            }
        }
        assertThat(pbxprojFile.existsIn(iOSProjRootDir.mockkedPath)).isTrue
    }

    @Test
    fun `Project pbxproj file is not discovered when only Pods dir contains pbxproj file`() {
        val iOSProjRootDir = dir("ProjectA") {
            dir("ProjectA.xcodeproj") {
            }
            dir("Pods.xcodeproj") {
                file("project.pbxproj")
            }
        }
        assertThat(pbxprojFile.existsIn(iOSProjRootDir.mockkedPath)).isFalse
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