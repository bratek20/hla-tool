package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.definitions.*
import pl.bratek20.hla.parsing.api.ModuleDefinitionsParser
import java.util.ArrayDeque

class ModuleDefinitionsParserImpl: ModuleDefinitionsParser {
    override fun parse(path: Path): List<ModuleDefinition> {
        val directories = DirectoriesLogic()

        val modulesDir = directories.readDirectory(path)
        return modulesDir.files.map { parseModuleFile(it) }
    }

    private fun parseModuleFile(file: File): ModuleDefinition {
        val moduleName = ModuleName(file.name.split(".module").get(0))
        val elements = parseElements(file.content)
        val valueObjects = parseValueObjects(elements)
        val interfaces = parseInterfaces(elements)
        val propertyValueObjects = parsePropertyValueObjects(elements)
        return ModuleDefinition(
            name = moduleName,
            simpleValueObjects = valueObjects.simple,
            complexValueObjects = valueObjects.complex,
            interfaces = interfaces,
            propertyValueObjects = propertyValueObjects,
            propertyMappings = emptyList()
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
        val simple: List<SimpleStructureDefinition>,
        val complex: List<ComplexStructureDefinition>
    )
    private fun parseValueObjects(elements: List<ParsedElement>): ValueObjects {
        val voSection = elements.find { it is Section && it.name == "ValueObjects" } as Section?
        val simple = voSection?.elements?.filterIsInstance<Assignment>()?.map {
            SimpleStructureDefinition(
                name = it.name,
                typeName = it.value
            )
        } ?: emptyList()

        val complex = parseComplexStructureDefinitions(voSection)

        return ValueObjects(
            simple = simple,
            complex = complex
        )
    }

    private fun parseType(typeValue: String): TypeDefinition {
        if (typeValue.contains("[]")) {
            return TypeDefinition(
                name = typeValue.replace("[]", ""),
                wrappers = listOf(TypeWrapper.LIST)
            )
        }
        return TypeDefinition(
            name = typeValue,
        )
    }
    private fun parseComplexValueObject(section: Section): ComplexStructureDefinition {
        return ComplexStructureDefinition(
            name = section.name,
            fields = section.elements.filterIsInstance<Assignment>().map {
                FieldDefinition(
                    name = it.name,
                    type = parseType(it.value)
                )
            }
        )
    }

    private fun parseMethod(method: ParsedMethod): MethodDefinition {
        return MethodDefinition(
            name = method.name,
            returnType = method.returnType?.let { parseType(it) },
            args = method.args.map {
                ArgumentDefinition(
                    name = it.name,
                    type = parseType(it.value)
                )
            },
            throws = listOf()
        )
    }

    private fun parseInterfaces(elements: List<ParsedElement>): List<InterfaceDefinition> {
        val interfacesSection = elements.find { it is Section && it.name == "Interfaces" } as Section?
        return interfacesSection?.elements?.filterIsInstance<Section>()?.map {
            InterfaceDefinition(
                name = it.name,
                methods = it.elements.filterIsInstance<ParsedMethod>().map {
                    parseMethod(it)
                }
            )
        } ?: emptyList()
    }

    private fun parsePropertyValueObjects(elements: List<ParsedElement>): List<ComplexStructureDefinition> {
        val propertyVosSection = elements.find { it is Section && it.name == "PropertyValueObjects" } as Section?
        return parseComplexStructureDefinitions(propertyVosSection)
    }

    private fun parseComplexStructureDefinitions(section: Section?): List<ComplexStructureDefinition> {
        return section?.elements?.filterIsInstance<Section>()?.map {
            parseComplexValueObject(it)
        } ?: emptyList()
    }
}