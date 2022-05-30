package com.andro.spreadi18ncore.filewriting

import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path

class AndroidTranslationFileWriter(private val targetDirectoryPath: Path) :
    TranslationFileWriter {

    private val localizableFilePath: Path by lazy {
        targetDirectoryPath.resolve("strings.xml")
    }
    private val localizableWriter: BufferedWriter by lazy {
        val writer = Files.newBufferedWriter(localizableFilePath)
        writer.attachXmlOpeningTag()
        writer
    }

    override fun write(key: String, value: String) {
        if (key.startsWith("//")) {
            localizableWriter.write("    <!-- ${key.replace("// *".toRegex(), "")} -->\n")
        } else if (key.isNotBlank()) {
            localizableWriter.write("    <string name=\"$key\">$value</string>\n")
        }
    }

    override fun close() {
        localizableWriter.attachXmlClosingTag()
        localizableWriter.flush()
        localizableWriter.close()
    }

    private fun BufferedWriter.attachXmlOpeningTag() {
        this.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        this.write("<resources>\n")
    }
    private fun BufferedWriter.attachXmlClosingTag() {
        this.write("</resources>\n")
    }
}