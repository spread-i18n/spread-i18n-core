package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.Project
import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.targetproject.ProjectType

internal data class ImportEvaluation(
    val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets
)

internal class ImportEvaluator {

    fun evaluate(headerRow: HeaderRow, targetProject: Project): ImportEvaluation {
        val targetDirectories = targetProject.localizationDirectories
        val matchResult = targetProject.type.sourceTargetMatcher.match(headerRow.localeCells, targetDirectories)
        return ImportEvaluation(targetProject.type, matchResult)
    }
}