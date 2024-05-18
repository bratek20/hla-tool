package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.facade.api.ModuleName
import pl.bratek20.hla.parsing.api.ModuleDefinitionsParser
import java.util.ArrayDeque

class ModuleDefinitionsParserLogic: ModuleDefinitionsParser {
    override fun parse(path: Path): List<ModuleDefinition> {
        val directories = DirectoriesLogic()

        val modulesDir = directories.readDirectory(path)
        return modulesDir.files
            .filter { it.name.endsWith(".module") }
            .map { parseModuleFile(it) }
    }

    private fun parseModuleFile(file: File): ModuleDefinition {
        val moduleName = ModuleName(file.name.split(".module").get(0))
        val elements = parseElements(file.content)
        val namedTypes = parseSimpleStructureDefinitions("NamedTypes", elements)
        val complexValueObjects = parseComplexStructureDefinitions("ValueObjects", elements)
        val interfaces = parseInterfaces(elements)
        val properties = parseProperties(elements)
        val enums = parseEnums(elements)
        val customTypes = parseStructures("CustomTypes", elements)

        return ModuleDefinition(
            name = moduleName,
            simpleValueObjects = namedTypes,
            complexValueObjects = complexValueObjects,
            interfaces = interfaces,
            propertyValueObjects = properties.vos,
            propertyMappings = properties.mappings,
            enums = enums,
            simpleCustomTypes = customTypes.simple,
            complexCustomTypes = customTypes.complex
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

        fun addElements(elements: List<ParsedElement>) {
            this.elements.addAll(elements)
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
    ) : ParsedNode(indent)

    private fun removeComments(line: String): String {
        return line.substringBefore("//")
    }

    private fun parseElements(content: FileContent): List<ParsedElement> {
        val initialElements = content.lines
            .map { removeComments(it) }
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

    data class Structures(
        val simple: List<SimpleStructureDefinition>,
        val complex: List<ComplexStructureDefinition>
    )
    private fun parseStructures(sectionName: String, elements: List<ParsedElement>): Structures {
        val simple = parseSimpleStructureDefinitions(sectionName, elements)
        val complex = parseComplexStructureDefinitions(sectionName, elements)

        return Structures(
            simple = simple,
            complex = complex
        )
    }

    private fun parseSimpleStructureDefinitions(sectionName: String, elements: List<ParsedElement>): List<SimpleStructureDefinition> {
        val voSection = elements.find { it is Section && it.name == sectionName } as Section?
        return voSection?.elements?.filterIsInstance<Assignment>()?.map {
            SimpleStructureDefinition(
                name = it.name,
                typeName = it.value
            )
        } ?: emptyList()
    }

    private fun parseComplexStructureDefinitions(sectionName: String, elements: List<ParsedElement>): List<ComplexStructureDefinition> {
        val voSection = elements.find { it is Section && it.name == sectionName } as Section?
        return parseComplexStructureDefinitions(voSection)
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
    private fun parseComplexStructureDefinition(section: Section): ComplexStructureDefinition {
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

    data class Properties(
        val vos: List<ComplexStructureDefinition>,
        val mappings: List<PropertyMapping>
    )
    private fun parseProperties(elements: List<ParsedElement>): Properties {
        val vos: MutableList<ComplexStructureDefinition> = mutableListOf()
        val mappings: MutableList<PropertyMapping> = mutableListOf()

        val propertiesSection = elements.find { it is Section && it.name == "Properties" } as Section?
        propertiesSection?.elements?.forEach {
            if(it is Section) {
                vos.add(parseComplexStructureDefinition(it))
            } else if(it is ParsedMapping) {
                mappings.add(PropertyMapping(it.key, parseType(it.value)))

                val voSection = Section(it.indent, parseType(it.value).name)
                voSection.addElements(it.elements)
                vos.add(parseComplexStructureDefinition(voSection))
            }
        }

        return Properties(
            vos = vos,
            mappings = mappings
        )
    }

    private fun parseComplexStructureDefinitions(section: Section?): List<ComplexStructureDefinition> {
        return section?.elements?.filterIsInstance<Section>()?.map {
            parseComplexStructureDefinition(it)
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