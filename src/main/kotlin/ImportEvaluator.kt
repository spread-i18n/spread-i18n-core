import java.io.File
import java.lang.IllegalArgumentException


interface SourceTargetMatcher {
    fun match(sourceLocalizations: Collection<SourceColumn>, targetLocalizations: Collection<TargetDirectory>):
            MatchedSourcesAndTargets
}

val String.normalizedTag: String
    get() {
        return toLowerCase().replace("_", "-")
    }

class ImportEvaluator {

    fun evaluate(configRow: ConfigRow, targetProjectFile: File): ImportEvaluation {
        val projectType = establishProjectType(targetProjectFile)
        val targetDirectories = listTargetDirectories(inFile = targetProjectFile, projectType = projectType)
        val matchResult = projectType.matcher.match(configRow.sourceColumns, targetDirectories)
        return ImportEvaluation(projectType, matchResult)
    }

    private fun establishProjectType(targetProjectFile: File): ProjectType {
        val iOSProjectFileCandidates = targetProjectFile.listFiles { file -> file.name.endsWith(".xcodeproj") }
        if (iOSProjectFileCandidates.size > 1) {
            throw ImportException("More than one .xcodeproj directories found.")
        } else if (iOSProjectFileCandidates.size == 1) {
            return ProjectType.iOS
        }
        throw IllegalArgumentException("Unknown project type")
    }

    private fun listTargetDirectories(inFile: File, projectType: ProjectType): List<TargetDirectory> {
        return when (projectType) {
            ProjectType.iOS -> iOSListLocalizationDirs(inFile)
            ProjectType.Android -> androidListLocalizationDirs(inFile)
        }
    }

    private fun androidListLocalizationDirs(inFile: File): List<TargetDirectory> {
        TODO("Not yet implemented")
    }

    private fun iOSListLocalizationDirs(inFile: File): List<TargetDirectory> {
        fun allDirsRecursively(rootFile: File): List<File> {
            return rootFile.listFiles { file -> file.isDirectory }
                    .map { file -> listOf(file) + allDirsRecursively(file) }
                    .flatten()
        }
        return allDirsRecursively(inFile).filter { dir -> dir.name.endsWith(".lproj") }.map { TargetDirectory(it) }
    }
}