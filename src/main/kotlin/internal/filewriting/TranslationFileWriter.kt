package internal.filewriting

import internal.ImportException
import java.io.BufferedWriter
import java.io.Closeable
import java.io.IOException
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Path

internal class CanNotAccessLocalizationFile(exc: IOException) : ImportException(cause = exc)

internal interface TranslationFileWriter: Closeable {
    fun write(key: String, value: String)
}

