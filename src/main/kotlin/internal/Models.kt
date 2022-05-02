package internal

import java.nio.file.Path

fun <T> Sequence<T>.skipTo(n: Int): Sequence<T> = drop(n)

open class ImportException(message: String? = null, cause: Throwable? = null): Exception(message, cause)

internal data class ImportEvaluation(val projectType: ProjectType, val matchedSourcesAndTargets: MatchedSourcesAndTargets)

