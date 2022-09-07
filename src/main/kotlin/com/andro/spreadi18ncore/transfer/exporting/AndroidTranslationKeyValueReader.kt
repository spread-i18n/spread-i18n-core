package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.transfer.importing.InvalidAndroidTranslationFile
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.transform
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.transfer.withCommentIndicator
import com.andro.spreadi18ncore.transfer.withNonTranslatableIndicator
import org.apache.commons.io.input.ReaderInputStream
import org.w3c.dom.*
import org.w3c.dom.Node.COMMENT_NODE
import org.w3c.dom.Node.ELEMENT_NODE
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

internal class AndroidTranslationKeyValueReader(private val pathOfLocalizableFile: Path, usePlainReader: Boolean = true):
    TranslationKeyValueReader {
    private val localizableFilePath: Path by lazy {
        pathOfLocalizableFile.resolve("strings.xml")
    }

    private val internalReader: TranslationKeyValueReader

    init {
        val reader = Files.newBufferedReader(localizableFilePath)
        internalReader = if (usePlainReader) {
            PlainAndroidTranslationKeyValueReader(reader)
        } else {
            XmlAndroidTranslationKeyValueReader(reader)
        }
    }

    override fun read(valueTransformation: ValueTransformation?): KeyValue? {
        return internalReader.read(valueTransformation)
    }

    override fun close() {
        internalReader.close()
    }
}

internal class XmlAndroidTranslationKeyValueReader(private val bufferedReader: BufferedReader) :
    TranslationKeyValueReader {

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

    private val resourceNodes: NodeList by lazy {
        val resourceNodes = document.getElementsByTagName("resources")
        if (resourceNodes.length == 1) {
            val resource = resourceNodes.item(0)
            resource.childNodes
        } else throw InvalidAndroidTranslationFile("Expected single <resource></resource> node in Android translation file")
    }

    private var currentItem = 0

    override fun read(valueTransformation: ValueTransformation?): KeyValue? {
        while (currentItem < resourceNodes.length) {
            val node = resourceNodes.item(currentItem++)
            val keyValue = node.asKeyValue(valueTransformation)
            if (keyValue != null) {
                return keyValue
            }
        }
        return null
    }

    override fun close() {
    }

    private fun Node.asKeyValue(valueTransformation: ValueTransformation?): KeyValue? {
        return when (nodeType) {
            ELEMENT_NODE -> {
                val element = this as Element
                var key = element.getAttribute("name")
                element.getAttribute("translatable").let {
                    if (it == "false") {
                        key = key.withNonTranslatableIndicator
                    }
                }
                val value = element.textContent.transform(valueTransformation)
                KeyValue(key, value)
            }
            COMMENT_NODE -> {
                val comment = this as Comment
                KeyValue(comment.textContent.withCommentIndicator, "")
            }
            else -> null
        }
    }
}

internal class PlainAndroidTranslationKeyValueReader(private val bufferedReader: BufferedReader) :
    TranslationKeyValueReader {
    override fun read(valueTransformation: ValueTransformation?): KeyValue? {
        var line = bufferedReader.readLine()
        while (line != null) {
            val keyValue = line.asKeyValue(valueTransformation)
            if (keyValue != null) {
                return keyValue
            }
            line = bufferedReader.readLine()
        }
        return null
    }

    private fun String.asKeyValue(valueTransformation: ValueTransformation?): KeyValue? {
        return extractKeyValueFromStringResource(valueTransformation) ?: extractKeyValueFromComment()
    }

    private val xmlKeyValueRegex = Regex(""".*<string name="(\w+)"(\W+translatable="false")?[^>]*>(.+)</string>""")
    private fun String.extractKeyValueFromStringResource(valueTransformation: ValueTransformation?): KeyValue? {
            return xmlKeyValueRegex.matchEntire(this)?.groups?.filterNotNull()?.let { group ->
                when (group.size) {
                    3 -> makeKeyValue(group[1].value, group[2].value, valueTransformation)
                    4 -> makeKeyValue(group[1].value.withNonTranslatableIndicator, group[3].value, valueTransformation)
                    else -> null
                }
            }
        }

    private fun makeKeyValue(key: String, value: String, valueTransformation: ValueTransformation?): KeyValue {
        return KeyValue(key, value.unescaped.transform(valueTransformation))
    }

    private val xmlCommentRegex = Regex(""".*<!--(.+)-->""")
    private fun String.extractKeyValueFromComment(): KeyValue? {
        return xmlCommentRegex.matchEntire(this)?.groups?.filterNotNull()?.let { group ->
            when (group.size) {
                2 -> KeyValue(group[1].value.withCommentIndicator, "")
                else -> null
            }
        }
    }

    private val String.unescaped: String get() = AndroidEscaping.unescape(this)
    override fun close() {
        bufferedReader.close()
    }
}