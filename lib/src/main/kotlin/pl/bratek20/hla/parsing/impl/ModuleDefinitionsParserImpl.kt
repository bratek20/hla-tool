package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.definitions.api.*
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
        val propertyMappings = parsePropertyMappings(elements)
        val enums = parseEnums(elements)

        return ModuleDefinition(
            name = moduleName,
            simpleValueObjects = valueObjects.simple,
            complexValueObjects = valueObjects.complex,
            interfaces = interfaces,
            propertyValueObjects = propertyValueObjects,
            propertyMappings = propertyMappings,
            enums = enums
        )
    }

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
    }

    class Section(
        indent: Int,
        val name: String,
    ) : ParsedNode(indent) {
    }

    class Assignment(
        indent: Int,
        val name: String,
        val value: String
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
    ) : ParsedLeaf(indent)

    private fun parseElements(content: FileContent): List<ParsedElement> {
        val initialElements = content.lines
            .filter { it.isNotBlank() }
            .map { parseElement(it) }

        val result = mutableListOf<ParsedElement>()
        val nodesStack: ArrayDeque<ParsedNode> = ArrayDeque()

        for (element in initialElements) {
            if(element is ParsedNode) {
                while((nodesStack.lastOrNull()?.indent ?: -1) >= element.indent) {
                    nodesStack.removeLast()
                }

                if (nodesStack.isEmpty()) {
                    result.add(element)
                } else {
                    nodesStack.last().addElement(element)
                }

                nodesStack.add(element)
            } else {
                nodesStack.last.addElement(element)
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
        } else if(noIndentLine.contains("->"))  {
            noIndentLine.split("->").let {
                val key = it[0].replace("\"", "").trim()
                return ParsedMapping(indent, key, it[1].trim())
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
            wrappers = emptyList()
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
            returnType = method.returnType?.let { parseType(it) } ?: TypeDefinition("void", emptyList()),
            args = method.args.map {
                ArgumentDefinition(
                    name = it.name,
                    type = parseType(it.value)
                )
            },
            throws = parseExceptions(method.elements)
        )
    }

    private fun parseExceptions(elements: List<ParsedElement>): List<ExceptionDefinition> {
        val exceptionsSection = elements.find { it is Section && it.name == "throws" } as Section?
        return exceptionsSection?.elements?.filterIsInstance<Section>()?.map {
            ExceptionDefinition(
                name = it.name
            )
        } ?: emptyList()
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

    private fun parsePropertyMappings(elements: List<ParsedElement>): List<PropertyMapping> {
        val propertyMappingsSection = elements.find { it is Section && it.name == "Properties" } as Section?
        return propertyMappingsSection?.elements?.filterIsInstance<ParsedMapping>()?.map {
            PropertyMapping(
                key = it.key,
                type = parseType(it.value),
            )
        } ?: emptyList()
    }

    private fun parseComplexStructureDefinitions(section: Section?): List<ComplexStructureDefinition> {
        return section?.elements?.filterIsInstance<Section>()?.map {
            parseComplexValueObject(it)
        } ?: emptyList()
    }

    private fun parseEnums(elements: List<ParsedElement>): List<EnumDefinition> {
        val enumsSection = elements.find { it is Section && it.name == "Enums" } as Section?
        return enumsSection?.elements?.filterIsInstance<Section>()?.map {
            EnumDefinition(
                name = it.name,
                values = it.elements.filterIsInstance<Section>().map {
                    it.name
                }
            )
        } ?: emptyList()
    }
}