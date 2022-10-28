package com.andro.spreadi18ncore.transfer

import com.andro.spreadi18ncore.transfer.translation.TranslationsDestination
import com.andro.spreadi18ncore.transfer.translation.TranslationsSource

open class TransferException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

internal fun transfer(from: TranslationsSource, to: TranslationsDestination) {
    rename(from = from, to = { source ->
        rename(from = to, to = { destination ->
            val table = source.translationTableReader.read()
            destination.translationTableWriter.write(table)
        })
    })
}

internal class UnknownTransferError(exc: Throwable)
    : TransferException(message = exc.message ?: "Unknown transfer error", cause = exc)

internal inline fun <T, R> rename(from: T, to: (T) -> R): R {
    return to(from)
}

internal inline fun <R> tryBlock(block: () -> R): R =
    try {
        block()
    } catch (exc: TransferException) {
        throw exc
    } catch (exc: Throwable) {
        throw UnknownTransferError(exc)
    }

