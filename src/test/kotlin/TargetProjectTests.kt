import internal.AndroidManifest
import internal.xcodeprojDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TargetProjectTests {

    @Test
    fun `xcodeproj is discovered in directory hierarchy`() {
        val iOSProjRootDir = dir("app") {
            dir("ProjectA") {
                dir("ProjectA.xcodeproj"){
                }
            }
        }
        assertThat(xcodeprojDirectory.existsIn(iOSProjRootDir.mockkedPath)).isTrue
    }

    @Test
    fun `AndroidManifest is discovered in directory hierarchy`() {
        val androidProjRootDir = dir("app") {
            dir("src") {
                dir("main"){
                    dir("java") {
                    }
                    file("AndroidManifest.xml")
                }
            }
        }
        assertThat(AndroidManifest.existsIn(androidProjRootDir.mockkedPath)).isTrue
    }

    @Test
    fun `AndroidManifest is not discovered in directory hierarchy`() {
        val androidProjRootDir = dir("app") {
            dir("src") {
                dir("main"){
                    dir("java") {
                    }
                }
            }
        }
        assertThat(AndroidManifest.existsIn(androidProjRootDir.mockkedPath)).isFalse
    }
}