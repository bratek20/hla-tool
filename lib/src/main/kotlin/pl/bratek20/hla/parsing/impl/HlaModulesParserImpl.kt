package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.model.*
import pl.bratek20.hla.parsing.api.HlaModulesParser
import java.util.ArrayDeque
import java.util.Queue

class HlaModulesParserImpl: HlaModulesParser {
    override fun parse(path: Path): List<HlaModule> {
        val directories = DirectoriesLogic()

        val modulesDir = directories.readDirectory(path)
        return modulesDir.files.map { parseModuleFile(it) }
    }

    private fun parseModuleFile(file: File): HlaModule {
        val moduleName = ModuleName(file.name.split(".module").get(0))
        val elements = parseElements(file.content)
        val valueObjects = parseValueObjects(elements)
        val interfaces = parseInterfaces(elements)
        return HlaModule(
            name = moduleName,
            simpleValueObjects = valueObjects.simple,
            complexValueObjects = valueObjects.complex,
            interfaces = interfaces,
            properties = emptyList()
        )
    }

    open class ParsedElement(
        val indent: Int
    )

    class Section(
        indent: Int,
        val name: String,
        val elements: MutableList<ParsedElement> = mutableListOf()
    ) : ParsedElement(indent) {
        fun addElement(element: ParsedElement) {
            elements.add(element)
        }
    }

    class Assignment(
        indent: Int,
        val name: String,
        val value: String
    ) : ParsedElement(indent)

    class ParsedArg(
        val name: String,
        val value: String
    )
    class ParsedMethod(
        indent: Int,
        val name: String,
        val args: List<ParsedArg>,
        val returnType: String?
    ) : ParsedElement(indent)

    private fun parseElements(content: FileContent): List<ParsedElement> {
        val initialElements = content.lines
            .filter { it.isNotBlank() }
            .map { parseElement(it) }

        val result = mutableListOf<ParsedElement>()
        val sectionsStack: ArrayDeque<Section> = ArrayDeque()

        for (element in initialElements) {
            if(element is Section) {
                while((sectionsStack.lastOrNull()?.indent ?: -1) >= element.indent) {
                    sectionsStack.removeLast()
                }

                if (sectionsStack.isEmpty()) {
                    result.add(element)
                } else {
                    sectionsStack.last().addElement(element)
                }

                sectionsStack.add(element)
            } else {
                sectionsStack.last.addElement(element)
            }
        }

        return result
    }

    private fun parseElement(line: String): ParsedElement {
        val indent = line.takeWhile { it == ' ' }.length
        val noIndentLine = line.trim()

        if (noIndentLine.contains("(")) {
            val methodName = noIndentLine.substringBefore("(")
            val args = noIndentLine.substringAfter("(").substringBefore(")").split(",").map {
                val split = it.split(":")
                ParsedArg(split[0].trim(), split[1].trim())
            }
            val returnTypeStr = noIndentLine.substringAfter(")").trim()
            val returnType = returnTypeStr.contains(":").let {
                if(it) returnTypeStr.substringAfter(":").trim() else null
            }
            return ParsedMethod(indent, methodName, args, returnType)
        }
        else if(noIndentLine.contains(":"))  {
            noIndentLine.split(":").let {
                return Assignment(indent, it[0], it[1].trim())
            }
        } else {
            return Section(indent, noIndentLine)
        }
    }

    data class ValueObjects(
        val simple: List<SimpleValueObject>,
        val complex: List<ComplexValueObject>
    )
    private fun parseValueObjects(elements: List<ParsedElement>): ValueObjects {
        val voSection = elements.find { it is Section && it.name == "ValueObjects" } as Section?
        val simple = voSection?.elements?.filterIsInstance<Assignment>()?.map {
            SimpleValueObject(
                name = it.name,
                typeName = it.value
            )
        } ?: emptyList()

        val complex = voSection?.elements?.filterIsInstance<Section>()?.map {
            parseComplexValueObject(it)
        } ?: emptyList()

        return ValueObjects(
            simple = simple,
            complex = complex
        )
    }

    private fun parseType(typeValue: String): Type {
        if (typeValue.contains("[]")) {
            return Type(
                name = typeValue.replace("[]", ""),
                wrappers = listOf(TypeWrapper.LIST)
            )
        }
        return Type(
            name = typeValue,
        )
    }
    private fun parseComplexValueObject(section: Section): ComplexValueObject {
        return ComplexValueObject(
            name = section.name,
            fields = section.elements.filterIsInstance<Assignment>().map {
                Field(
                    name = it.name,
                    type = parseType(it.value)
                )
            }
        )
    }

    private fun parseMethod(method: ParsedMethod): Method {
        return Method(
            name = method.name,
            returnType = method.returnType?.let { parseType(it) },
            args = method.args.map {
                Argument(
                    name = it.name,
                    type = parseType(it.value)
                )
            },
            throws = listOf()
        )
    }

    private fun parseInterfaces(elements: List<ParsedElement>): List<Interface> {
        val interfacesSection = elements.find { it is Section && it.name == "Interfaces" } as Section?
        return interfacesSection?.elements?.filterIsInstance<Section>()?.map {
            Interface(
                name = it.name,
                methods = it.elements.filterIsInstance<ParsedMethod>().map {
                    parseMethod(it)
                }
            )
        } ?: emptyList()
    }
}