package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.sourcesheet.ColumnIndex
import com.andro.spreadi18ncore.targetproject.ProjectType

internal data class ImportDeclaration(val keyColumnIndex: ColumnIndex,
                                      val firstTranslationRow: Int,
                                      val matchedSourcesAndTargets: MatchedSourcesAndTargets,
                                      val projectType: ProjectType
)
