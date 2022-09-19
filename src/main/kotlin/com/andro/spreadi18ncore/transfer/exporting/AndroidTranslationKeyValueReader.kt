package com.andro.spreadi18ncore.transfer.exporting

import com.andro.spreadi18ncore.transfer.importing.InvalidAndroidTranslationFile
import com.andro.spreadi18ncore.transfer.base.TranslationKeyValueReader
import com.andro.spreadi18ncore.transfer.transformation.AndroidEscaping
import com.andro.spreadi18ncore.transfer.transformation.ValueTransformation
import com.andro.spreadi18ncore.transfer.transformation.transformed
import com.andro.spreadi18ncore.transfer.translation.KeyValue
import com.andro.spreadi18ncore.transfer.withArrayIndicator
import com.andro.spreadi18ncore.transfer.withCommentIndicator
import com.andro.spreadi18ncore.transfer.withNonTranslatableIndicator
import com.andro.spreadi18ncore.withNewLineIfNotBlank
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
                val value = element.textContent.transformed(valueTransformation)
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
        var keyValueCandidate = ""
        while (line != null) {
            keyValueCandidate += if (keyValueCandidate.isNotBlank()) {
                "\n$line"
            } else {
                line
            }
            val keyValue = keyValueCandidate.asKeyValue(valueTransformation)
            if (keyValue != null) {
                return keyValue
            }
            line = bufferedReader.readLine()
        }
        return null
    }

    private fun String.asKeyValue(valueTransformation: ValueTransformation?): KeyValue? {
        return XmlKeyValueExtractor.extract(this)?.let {
            KeyValue(it.key, it.value.trim().unescaped.transformed(by = valueTransformation))
        } ?: XmlCommentExtractor.extractComment(this)?.let {
            KeyValue(it.withCommentIndicator, "")
        }
    }

    private fun String.extractKeyValueFromComment(): KeyValue? {
        return XmlCommentExtractor.extractComment(this)?.let {
            KeyValue(it.withCommentIndicator, "")
        }
    }

    private val String.unescaped: String get() = AndroidEscaping.unescape(this)
    override fun close() {
        bufferedReader.close()
    }
}


internal object XmlKeyValueExtractor {

    fun extract(xmlValueCandidate: String): KeyValue? {
        return SingleValueExtractor.extract(xmlValueCandidate) ?: ArrayValueExtractor.extract(xmlValueCandidate)
    }
    private object SingleValueExtractor {
        private val regex = Regex("""[\s\S]*<string name="(\w+)"(\W+translatable="false")?[^>]*>([\s\S]+)</string>""")

        fun extract(singleXmlValueCandidate: String): KeyValue? {
            return regex.matchEntire(singleXmlValueCandidate)?.groups?.filterNotNull()?.let { group ->
                when (group.size) {
                    3 -> KeyValue(group[1].value, group[2].value)
                    4 -> KeyValue(group[1].value.withNonTranslatableIndicator, group[3].value)
                    else -> null
                }
            }
        }
    }

    private object ArrayValueExtractor {
        private val regex = Regex("""[\s\S]*<string-array name="(\w+)"(\W+translatable="false")?[^>]*>([\s\S]+)</string-array>""")

        fun extract(singleXmlValueCandidate: String): KeyValue? {
            return regex.matchEntire(singleXmlValueCandidate)?.groups?.filterNotNull()?.let { group ->
                when (group.size) {
                    3 -> KeyValue(group[1].value.withArrayIndicator,
                        itemsToMultilineString(group[2].value))

                    4 -> KeyValue(group[1].value.withNonTranslatableIndicator.withArrayIndicator,
                        itemsToMultilineString(group[3].value))
                    else -> null
                }
            }
        }
        private fun itemsToMultilineString(itemsXml: String): String {
            return itemsXml.split("<item>")
                .map { it.replace(Regex("""</item>[\s\S]*"""), "") }
                .fold(""){ multilineString, value -> multilineString.withNewLineIfNotBlank + value}
        }
    }
}
internal object XmlCommentExtractor {

    //+? matches the previous token between one and unlimited times, as few times as possible, expanding as needed (lazy)
    //\s matches any whitespace character (equivalent to [\r\n\t\f\v ])
    private val regex = Regex("""[\s\S]*<!--\s*(.+?)\s*-->[\s\S]*""")

    fun extractComment(xmlCommentCandidate: String):String? {
        return regex.matchEntire(xmlCommentCandidate)?.groups?.filterNotNull()?.let { group ->
            when (group.size) {
                2 -> group[1].value
                else -> null
            }
        }
    }
}