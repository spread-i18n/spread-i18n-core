package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.sourcesheet.HeaderRow
import com.andro.spreadi18ncore.sourcetargetmatching.MatchedSourcesAndTargets
import com.andro.spreadi18ncore.targetproject.ProjectType
import com.andro.spreadi18ncore.targetproject.TargetProject

internal data class ImportEvaluation(val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets)

internal class ImportEvaluator {

    fun evaluate(headerRow: HeaderRow, targetProject: TargetProject): ImportEvaluation {
        val targetDirectories = targetProject.localizationDirectories
        val matchResult = targetProject.type.sourceTargetMatcher.match(headerRow.sourceColumns, targetDirectories)
        return ImportEvaluation(targetProject.type, matchResult)
    }
}