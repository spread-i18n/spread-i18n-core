package internal


internal interface SourceTargetMatcher {
    fun match(sourceLocalizations: Collection<SourceColumn>, targetLocalizations: Collection<TargetDirectory>):
            MatchedSourcesAndTargets
}

val String.normalizedTag: String
    get() {
        return toLowerCase().replace("_", "-")
    }

internal class ImportEvaluator {

    fun evaluate(configRow: ConfigRow, targetProject: TargetProject): ImportEvaluation {
        val targetDirectories = targetProject.localizationDirectories
        val matchResult = targetProject.type.sourceTargetMatcher.match(configRow.sourceColumns, targetDirectories)
        return ImportEvaluation(targetProject.type, matchResult)
    }
}