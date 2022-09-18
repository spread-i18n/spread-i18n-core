package com.andro.spreadi18ncore.transfer

import com.andro.spreadi18ncore.transfer.translation.TranslationsDestination
import com.andro.spreadi18ncore.transfer.translation.TranslationsSource

open class TransferException(message: String? = null, cause: Throwable? = null): Exception(message, cause)
internal object Transfer {
    fun from(source: TranslationsSource): TransferPerformer {
        return TransferPerformer(source)
    }
    internal class TransferPerformer(private val source: TranslationsSource) {
        fun to(destination: TranslationsDestination) {
            val table = source.translationTableReader.read()
            destination.translationTableWriter.write(table)
        }
    }
}

internal class UnknownTransferError(exc: Exception) : TransferException(cause = exc)

internal inline fun <T, R> rename(from: T, to: (T) -> R): R {
    return to(from)
}

internal inline fun <R> tryBlock(block: () -> R): R =
    try {
        block()
    } catch (exc: TransferException) {
        throw exc
    } catch (exc: Exception) {
        throw UnknownTransferError(exc)
    }

