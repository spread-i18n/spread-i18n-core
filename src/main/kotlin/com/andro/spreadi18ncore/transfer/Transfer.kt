package com.andro.spreadi18ncore.transfer

import com.andro.spreadi18ncore.excel.TransferException
import com.andro.spreadi18ncore.transfer.translation.TranslationsDestination
import com.andro.spreadi18ncore.transfer.translation.TranslationsSource

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

internal inline fun <T, R> rename(obj: T, to: (T) -> R): R {
    return to(obj)
}

internal inline fun <R> tryBlock(block: () -> R): R =
    try {
        block()
    } catch (exc: TransferException) {
        throw exc
    } catch (exc: Exception) {
        throw UnknownTransferError(exc)
    }

