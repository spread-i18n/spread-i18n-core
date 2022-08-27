package com.andro.spreadi18ncore.export

import com.andro.spreadi18ncore.filewriting.InvalidAndroidTranslationFile
import com.andro.spreadi18ncore.valuetransformation.ValueTransformation
import com.andro.spreadi18ncore.valuetransformation.transform
import org.w3c.dom.Comment
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Node.COMMENT_NODE
import org.w3c.dom.Node.ELEMENT_NODE
import org.w3c.dom.NodeList
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

internal class AndroidTranslationKeyValueReader(private val pathOfLocalizableFile: Path) :
    TranslationKeyValueReader {
    private val localizableFilePath: Path by lazy {
        pathOfLocalizableFile.resolve("strings.xml")
    }

    private val resourceNodes: NodeList by lazy {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.parse(localizableFilePath.toFile())
        document.documentElement.normalize()
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
                val key = element.getAttribute("name")
                val value = element.textContent.transform(valueTransformation)
                KeyValue(key, value)
            }
            COMMENT_NODE -> {
                val comment = this as Comment
                KeyValue("//${comment.textContent}", "")
            }
            else -> null
        }
    }
}