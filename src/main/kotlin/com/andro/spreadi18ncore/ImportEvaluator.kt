package com.andro.spreadi18ncore

internal data class ImportEvaluation(val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets)

internal class ImportEvaluator {

    fun evaluate(configRow: ConfigRow, targetProject: TargetProject): ImportEvaluation {
        val targetDirectories = targetProject.localizationDirectories
        val matchResult = targetProject.type.sourceTargetMatcher.match(configRow.sourceColumns, targetDirectories)
        return ImportEvaluation(targetProject.type, matchResult)
    }
}