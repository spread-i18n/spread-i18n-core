package com.andro.spreadi18ncore.export

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

internal class AndroidTranslationKeyValueReader(private val pathOfLocalizableFile: Path) :
    TranslationKeyValueReader {
    private val localizableFilePath: Path by lazy {
        pathOfLocalizableFile.resolve("strings.xml")
    }

    private val nodeList: NodeList by lazy {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(localizableFilePath.toFile())
        doc.documentElement.normalize()
        doc.getElementsByTagName("string")
    }

    private var currentItem = 0

    override fun read(): KeyValue? {
        return if (currentItem < nodeList.length) {
            val node = nodeList.item(currentItem)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                val key = element.getAttribute("name")
                val value = element.textContent
                currentItem += 1
                KeyValue(key, value)
            } else null
        } else null
    }

    override fun close() {
    }
}