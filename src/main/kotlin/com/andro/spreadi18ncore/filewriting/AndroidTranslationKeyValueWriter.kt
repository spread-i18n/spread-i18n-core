package com.andro.spreadi18ncore.filewriting

import com.andro.spreadi18ncore.export.KeyValue
import com.andro.spreadi18ncore.sourcesheet.ImportException
import com.andro.spreadi18ncore.valuetransformation.AndroidDefaultValueTransformation
import org.apache.commons.io.input.ReaderInputStream
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


internal class AndroidTranslationKeyValueWriter(
    private val pathOfLocalizationFile: Path,
    usePlainWriter: Boolean = false
) :
    TranslationKeyValueWriter {

    private val localizableFilePath: Path by lazy {
        pathOfLocalizationFile.resolve("strings.xml")
    }

    private val internalWriter: TranslationKeyValueWriter

    init {
        val writer = Files.newBufferedWriter(localizableFilePath)
        internalWriter = if (usePlainWriter) {
            PlainAndroidTranslationKeyValueWriter(writer)
        } else {
            val reader = Files.newBufferedReader(localizableFilePath)
            XmlAndroidTranslationKeyValueWriter(reader, writer)
        }
    }

    override fun write(keyValue: KeyValue) {
        internalWriter.write(keyValue)
    }

    override fun close() {
        internalWriter.close()
    }
}

internal class PlainAndroidTranslationKeyValueWriter(private val bufferedWriter: BufferedWriter) :
    TranslationKeyValueWriter {

    init {
        bufferedWriter.attachXmlOpeningTag()
    }

    override fun write(keyValue: KeyValue) {
        with(keyValue) {
            if (key.startsWith("//")) {
                bufferedWriter.write("    <!-- ${key.replace("// *".toRegex(), "")} -->\n")
            } else if (key.isNotBlank()) {
                val xmlValue = AndroidDefaultValueTransformation.transform(value)
                bufferedWriter.write("    <string name=\"$key\">$xmlValue</string>\n")
            }
        }
    }

    override fun close() {
        bufferedWriter.attachXmlClosingTag()
        bufferedWriter.flush()
        bufferedWriter.close()
    }

    private fun BufferedWriter.attachXmlOpeningTag() {
        this.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        this.write("<resources>\n")
    }

    private fun BufferedWriter.attachXmlClosingTag() {
        this.write("</resources>\n")
    }
}

internal class InvalidAndroidTranslationFile(message: String) : ImportException(message = message)
internal class XmlAndroidTranslationKeyValueWriter(
    private val bufferedReader: BufferedReader,
    private val bufferedWriter: BufferedWriter
) : TranslationKeyValueWriter {

    private val document: Document by lazy {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        ReaderInputStream(bufferedReader, Charsets.UTF_8).use { inputStream ->
            if (inputStream.available() > 0) {
                builder.parse(inputStream).apply {
                    documentElement.normalize()
                }
            } else {
                builder.newDocument().apply {
                    appendChild(createElement("resources"))
                }
            }
        }
    }

    private val resourceNode: Node by lazy {
        val resourceNodes = document.getElementsByTagName("resources")
        if (resourceNodes.length == 1) {
            resourceNodes.item(0)
        } else throw InvalidAndroidTranslationFile("Expected single <resource></resource> node in Android translation file")
    }

    init {
        val stringNodes = resourceNode.childNodes
        for (i in 0 until stringNodes.length) {
            val stringNode = stringNodes.item(0)
            resourceNode.removeChild(stringNode)
        }
    }

    override fun write(keyValue: KeyValue) {
        val string = document.createElement("string")
        string.setAttribute("name", keyValue.key)
        string.appendChild(document.createTextNode(keyValue.value))
        resourceNode.appendChild(string)
    }

    override fun close() {
        val transformer = TransformerFactory.newInstance().newTransformer()
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(DOMSource(document), StreamResult(bufferedWriter))
        bufferedWriter.close()
    }
}
