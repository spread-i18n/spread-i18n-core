import java.io.File

val File.dirs: Array<File>
    get() = this.listFiles { file -> file.isDirectory }

interface LocalizationDirFinder {
    fun findLocalizationDirsIn(rootFile: File): List<TargetDirectory>
}

@Suppress("ClassName")
class iOSLocalizationDirFinder: LocalizationDirFinder {
    override fun findLocalizationDirsIn(rootFile: File): List<TargetDirectory> {
        fun allDirsRecursively(rootFile: File): List<File> {
            return rootFile.dirs
                    .map { file ->
                        listOf(file) + allDirsRecursively(file) }
                    .flatten()
        }
        return allDirsRecursively(rootFile).filter { dir -> dir.name.endsWith(".lproj") }.map { TargetDirectory(it) }
    }
}

class AndroidLocalizationDirFinder: LocalizationDirFinder {
    override fun findLocalizationDirsIn(rootFile: File): List<TargetDirectory> = emptyList()
}
