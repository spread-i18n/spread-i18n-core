package internal

import java.io.File


internal interface SourceTargetMatcher {
    fun match(sourceLocalizations: Collection<SourceColumn>, targetLocalizations: Collection<TargetDirectory>):
            MatchedSourcesAndTargets
}

val String.normalizedTag: String
    get() {
        return toLowerCase().replace("_", "-")
    }

internal class UnknownProjectType: ImportException("Unknown project type.")

internal class ImportEvaluator {

    fun evaluate(configRow: ConfigRow, targetProjectFile: File): ImportEvaluation {
        val projectType = establishProjectType(targetProjectFile)
        val targetDirectories = projectType.directoryFinder.findLocalizationDirsIn(targetProjectFile)
        val matchResult = projectType.sourceTargetMatcher.match(configRow.sourceColumns, targetDirectories)
        return ImportEvaluation(projectType, matchResult)
    }

    private fun establishProjectType(targetProjectFile: File): ProjectType {
        val iOSProjectFileCandidates = targetProjectFile.listFiles { file -> file.name.endsWith(".xcodeproj") }
        if (iOSProjectFileCandidates.size == 1) {
            return ProjectType.iOS
        }
        throw UnknownProjectType()
    }
}