package com.github.bratek20.hla.parsing.impl

import com.github.bratek20.hla.definitions.api.Attribute
import com.github.bratek20.hla.directory.api.FileContent
import java.util.ArrayDeque

open class ParsedElement(
    val indent: Int
)

open class ParsedLeaf(
    indent: Int,
) : ParsedElement(indent)

open class ParsedNode(
    indent: Int
) : ParsedElement(indent) {
    val elements: MutableList<ParsedElement> = mutableListOf()

    fun addElement(element: ParsedElement) {
        elements.add(element)
    }

    fun addElements(elements: List<ParsedElement>) {
        this.elements.addAll(elements)
    }
}

class Section(
    indent: Int,
    val name: String,
) : ParsedNode(indent) {
}

class ColonAssignment(
    indent: Int,
    val name: String,
    val value: String,
    val value2: String? = null,
    val defaultValue: String? = null,
    val attributes: List<Attribute> = emptyList()
) : ParsedLeaf(indent)

class EqualsAssignment(
    indent: Int,
    val name: String,
    val value: String,
) : ParsedLeaf(indent)

class ParsedArg(
    val name: String,
    val value: String
)
class ParsedMethod(
    indent: Int,
    val name: String,
    val args: List<ParsedArg>,
    val returnType: String?
) : ParsedNode(indent)

class ParsedMapping(
    indent: Int,
    val key: String,
    val value: String
) : ParsedNode(indent)

class ParsingEngine {
    fun parseElements(content: FileContent): List<ParsedElement> {
        val initialElements = content.lines
            .map { replaceTabsWithSpaces(it) }
            .map { removeComments(it) }
            .filter { it.isNotBlank() }
            .map { parseElement(it) }

        val result = mutableListOf<ParsedElement>()
        val nodesStack: ArrayDeque<ParsedNode> = ArrayDeque()

        for (element in initialElements) {
            while((nodesStack.lastOrNull()?.indent ?: -1) >= element.indent) {
                nodesStack.removeLast()
            }
            if(element is ParsedNode) {
                if (nodesStack.isEmpty()) {
                    result.add(element)
                } else {
                    nodesStack.last.addElement(element)
                }

                nodesStack.add(element)
            } else {
                nodesStack.last.addElement(element)
            }
        }

        return result
    }

    private fun removeComments(line: String): String {
        // search for first // that is not preceded by :
        val regex = Regex("""(?<!:)//""")
        val matchResult = regex.find(line)
        return if (matchResult != null) {
            line.substring(0, matchResult.range.first)
        } else {
            line
        }
    }

    private fun replaceTabsWithSpaces(line: String): String {
        return line.replace("\t", "    ")
    }
    
    private fun parseElement(line: String): ParsedElement {
        val indent = line.takeWhile { it == ' ' }.length
        val noIndentLine = line.trim()
        val firstEqualsSignIndex = noIndentLine.indexOf("=").takeIf { it != -1 } ?: Int.MAX_VALUE
        val firstColonSignIndex = noIndentLine.indexOf(":").takeIf { it != -1 } ?: Int.MAX_VALUE

        if (noIndentLine.contains(Regex("[a-zA-Z0-9]\\("))) {
            val methodName = noIndentLine.substringBefore("(")
            val args = noIndentLine.substringAfter("(").substringBefore(")").split(",")
                .filter { it.isNotBlank() }
                .map {
                    val split = it.split(":")
                    require(split.size == 2) { "Invalid argument definition: $it" }
                    ParsedArg(split[0].trim(), split[1].trim())
                }
            val returnTypeStr = noIndentLine.substringAfter(")").trim()
            val returnType = returnTypeStr.contains(":").let {
                if(it) returnTypeStr.substringAfter(":").trim() else null
            }
            return ParsedMethod(indent, methodName, args, returnType)
        }
        else if(firstEqualsSignIndex < firstColonSignIndex)  {
            noIndentLine.split("=").let {
                return EqualsAssignment(indent, it[0].trim(), it[1].trim())
            }
        }
        else if(noIndentLine.contains(":"))  {
            val name = noIndentLine.substringBefore(":").trim()
            var rest = noIndentLine.substringAfter(":").trim()
            var defaultValue: String? = null
            var attributes: List<Attribute> = emptyList()
            if (rest.contains("(")) {
                attributes = rest.substringAfter("(").substringBefore(")").split(",")
                    .filter { it.isNotBlank() }
                    .map {
                        var attName = it
                        var attValue = "true"
                        if(it.contains(":")) {
                            attName = it.substringBefore(":").trim()
                            attValue = it.substringAfter(":").trim()
                        }
                        Attribute(attName, attValue)
                    }
                rest = rest.substringBefore("(").trim()
            }
            if (rest.contains("=")) {
                defaultValue = rest.substringAfter("=").trim()
                rest = rest.substringBefore("=").trim()
            }

            val value: String
            val value2: String?
            if (rest.contains(":")) {
                val split = rest.split(":")
                value = split[0].trim()
                value2 = split[1].trim()
            } else {
                value = rest.trim()
                value2 = null
            }

            return ColonAssignment(
                indent = indent,
                name = name,
                value = value,
                value2 = value2,
                attributes = attributes,
                defaultValue = defaultValue
            )
        } else if(noIndentLine.contains("->"))  {
            noIndentLine.split("->").let {
                val key = it[0].replace("\"", "").trim()
                return ParsedMapping(indent, key, it[1].trim())
            }
        } else {
            return Section(indent, noIndentLine)
        }
    }
}