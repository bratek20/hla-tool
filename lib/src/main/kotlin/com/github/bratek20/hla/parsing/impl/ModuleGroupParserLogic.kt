package com.github.bratek20.hla.parsing.impl

import com.github.bratek20.architecture.properties.impl.PropertiesLogic
import com.github.bratek20.architecture.properties.sources.yaml.YamlPropertiesSource
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.directory.api.File
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.directory.api.FileName
import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.directory.impl.DirectoriesLogic
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.facade.api.PROFILES_KEY
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.UnknownRootSectionException
import java.util.*

class ModuleGroupParserLogic: ModuleGroupParser {
    private val directories = DirectoriesLogic()

    override fun parse(hlaFolderPath: Path, profileName: ProfileName): ModuleGroup {
        return parseModuleGroup(hlaFolderPath, profileName)
    }

    private fun parseModuleGroup(hlaFolder: Path, profileName: ProfileName): ModuleGroup {
        val properties = PropertiesLogic(emptySet())
        properties.addSource(YamlPropertiesSource(hlaFolder.add(FileName("properties.yaml")).value))
        val profile = properties.get(PROFILES_KEY).find { it.getName() == profileName } ?: throw IllegalArgumentException("Profile $profileName not found")

        val modulesDir = directories.read(hlaFolder)
        val modules = modulesDir.getFiles()
            .filter { it.getName().value.endsWith(".module") }
            .map { parseModuleFile(it) }

        val dependencies = profile.getImports().map {
            parseModuleGroup(hlaFolder.add(it.getHlaFolderPath()), it.getProfileName())
        }

        return ModuleGroup.create(
            name = GroupName(modulesDir.getName().value),
            modules = modules,
            profile = profile,
            dependencies = dependencies
        )
    }

    private fun parseModuleFile(file: File): ModuleDefinition {
        val moduleName = ModuleName(file.getName().value.split(".module").get(0))
        val elements = parseElements(file.getContent())
        checkRootSections(moduleName, elements)

        val valueObjects = parseStructures("ValueObjects", elements)
        val dataClasses = parseComplexStructureDefinitions("DataClasses", elements)
        val interfacesOutput = parseInterfaces(elements)
        val propertyKeys = parseKeys("PropertyKeys", elements)
        val enums = parseEnums(elements)
        val customTypes = parseStructures("CustomTypes", elements)
        val dataKeys = parseKeys("DataKeys", elements)
        val implSubmodule = parseImplSubmodule(elements)
        val externalTypes = parseExternalTypes(elements)
        val kotlinConfig = parseKotlinConfig(elements)
        val properties = parseProperties(elements)
        val data = parseData(elements)

        return ModuleDefinition.create(
            name = moduleName,
            simpleValueObjects = valueObjects.simple
                    + properties.valueObjects.simple,
            complexValueObjects = valueObjects.complex
                    + interfacesOutput.complexValueObjects
                    + properties.valueObjects.complex,
            interfaces = interfacesOutput.interfaces,
            propertyKeys = propertyKeys
                    + properties.keys,
            enums = enums,
            simpleCustomTypes = customTypes.simple,
            complexCustomTypes = customTypes.complex,
            dataClasses = dataClasses
                    + data.classes,
            dataKeys = dataKeys
                    + data.keys,
            implSubmodule = implSubmodule,
            externalTypes = externalTypes,
            kotlinConfig = kotlinConfig,
            webSubmodule = null
        )
    }

    data class ParsedProperties(
        val valueObjects: Structures,
        val keys: List<KeyDefinition>,
    )
    private fun parseProperties(elements: List<ParsedElement>): ParsedProperties {
        return ParsedProperties(
            valueObjects = parseStructures("Properties", elements),
            keys = parseKeys("Properties", elements)
        )
    }

    data class ParsedData(
        val classes: List<ComplexStructureDefinition>,
        val keys: List<KeyDefinition>,
    )
    private fun parseData(elements: List<ParsedElement>): ParsedData {
        return ParsedData(
            classes = parseComplexStructureDefinitions("Data", elements),
            keys = parseKeys("Data", elements)
        )
    }

    private fun parseExternalTypes(elements: List<ParsedElement>): List<String> {
        val externalTypeSection = elements.find { it is Section && it.name == "ExternalTypes" } as Section?
        if (externalTypeSection == null) {
            return emptyList()
        }

        return externalTypeSection.elements.filterIsInstance<Section>().map {
            it.name
        }
    }

    private fun parseKotlinConfig(elements: List<ParsedElement>): KotlinConfig? {
        val kotlinSection = elements.find { it is Section && it.name == "Kotlin" } as Section?
        if (kotlinSection != null) {
            val externalTypePackagesSection = kotlinSection.elements.find { it is Section && it.name == "ExternalTypePackages" } as Section?
            val mappings = externalTypePackagesSection?.elements?.filterIsInstance<ParsedMapping>()?.map {
                ExternalTypePackageMapping(it.key, it.value)
            }
            return KotlinConfig(
                externalTypePackages = mappings ?: emptyList()
            )
        }
        return null
    }

    private fun checkRootSections(module: ModuleName, elements: List<ParsedElement>) {
        val rootSections = elements.filterIsInstance<Section>()
        val knownRootSections = setOf(
            "ValueObjects",
            "DataClasses",
            "Interfaces",
            "PropertyKeys",
            "Enums",
            "CustomTypes",
            "DataKeys",
            "Impl",
            "ExternalTypes",
            "Kotlin",
            "Data",
            "Properties"
        )
        val unknownRootSections = rootSections.map { it.name }.filter { it !in knownRootSections }
        if (unknownRootSections.isNotEmpty()) {
            throw UnknownRootSectionException("Module ${module.value} has unknown root sections: $unknownRootSections")
        }
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
        val value: String,
        val defaultValue: String? = null,
        val attributes: List<Attribute>
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

        if (noIndentLine.contains(Regex("[a-zA-Z]\\("))) {
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
            return Assignment(
                indent = indent,
                name = name,
                value = rest.trim(),
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
                typeName = it.value,
                attributes = it.attributes
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
        if (typeValue.contains("?")) {
            return TypeDefinition(
                name = typeValue.replace("?", ""),
                wrappers = listOf(TypeWrapper.OPTIONAL)
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
                    type = parseType(it.value),
                    attributes = it.attributes,
                    defaultValue = it.defaultValue
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

    data class ParseInterfaceOutput(
        val interfaces: List<InterfaceDefinition>,
        val complexValueObjects: List<ComplexStructureDefinition>
    )
    private fun parseInterfaces(elements: List<ParsedElement>): ParseInterfaceOutput {
        val interfacesSection = elements.find { it is Section && it.name == "Interfaces" } as Section?

        val valueObjects = mutableListOf<ComplexStructureDefinition>()

        val interfaces = interfacesSection?.elements?.filterIsInstance<Section>()?.map {
            it.elements.filterIsInstance<Section>().forEach {
                valueObjects.add(parseComplexStructureDefinition(it))
            }

            InterfaceDefinition(
                name = it.name,
                methods = it.elements.filterIsInstance<ParsedMethod>().map {
                    parseMethod(it)
                }
            )
        } ?: emptyList()

        return ParseInterfaceOutput(
            interfaces = interfaces,
            complexValueObjects = valueObjects
        )
    }

    private fun parseKeys(sectionName: String, elements: List<ParsedElement>): List<KeyDefinition> {
        val section = elements.find { it is Section && it.name == sectionName } as Section?

        val keys: MutableList<KeyDefinition> = mutableListOf()
        section?.elements?.forEach {
            if(it is ParsedMapping) {
                keys.add(KeyDefinition(it.key, parseType(it.value)))
            }
        }
        return keys
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

    private fun parseImplSubmodule(elements: List<ParsedElement>): ImplSubmoduleDefinition? {
        val implSection = elements.find { it is Section && it.name == "Impl" } as Section?
        return implSection?.let {
            val dataClasses = parseComplexStructureDefinitions("DataClasses", implSection.elements)
            val keys = parseKeys("DataKeys", implSection.elements)
            val data = parseData(implSection.elements)
            return ImplSubmoduleDefinition(
                dataClasses = dataClasses + data.classes,
                dataKeys = keys + data.keys
            )
        }
    }


}