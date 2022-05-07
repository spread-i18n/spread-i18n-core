package com.andro.spreadi18ncore.importing

import com.andro.spreadi18ncore.sourcetargetmatching.MatchedSourcesAndTargets
import com.andro.spreadi18ncore.targetproject.ProjectType

internal data class ImportConfiguration(val keyColumn: Int,
                                        val firstTranslationRow: Int,
                                        val matchedSourcesAndTargets: MatchedSourcesAndTargets,
                                        val projectType: ProjectType
)
