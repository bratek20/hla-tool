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
import com.github.bratek20.logs.api.Logger
import java.util.*

class ModuleGroupParserLogic(
    private val log: Logger
): ModuleGroupParser {
    private val knownRootSections = setOf(
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
        "Properties",
        "Web"
    )

    private val directories = DirectoriesLogic()
    private val engine = ParsingEngine()

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
        log.info("Parsing module ${moduleName.value}")

        val elements = engine.parseElements(file.getContent())
        checkRootSections(moduleName, elements)

        val valueObjects = parseStructures("ValueObjects", elements)
        val dataClasses = parseComplexStructureDefinitions("DataClasses", elements)
        val interfacesOutput = parseInterfaces(elements)
        val propertyKeys = parseKeys("PropertyKeys", elements)
        val enums = parseEnums(elements)
        val customTypes = parseStructures("CustomTypes", elements)
        val dataKeys = parseKeys("DataKeys", elements)
        val properties = parseProperties(elements)
        val data = parseData(elements)

        return ModuleDefinition.create(
            name = moduleName,
            simpleValueObjects = valueObjects.simple
                    + properties.valueObjects.simple,
            complexValueObjects = valueObjects.complex
                    + interfacesOutput.complexValueObjects
                        .map { it.definition }
                    + properties.valueObjects.complex,
            interfaces = interfacesOutput.interfaces,
            propertyKeys = propertyKeys
                    + properties.keys,
            enums = enums,
            simpleCustomTypes = customTypes.simple,
            complexCustomTypes = customTypes.complex,
            dataClasses = dataClasses.map { it.definition }
                    + data.classes.map { it.definition },
            dataKeys = dataKeys
                    + data.keys,
            implSubmodule = parseImplSubmodule(elements),
            externalTypes = parseExternalTypes(elements),
            kotlinConfig = parseKotlinConfig(elements),
            webSubmodule = parseWebSubmodule(elements)
        )
    }

    private fun parseWebSubmodule(elements: List<ParsedElement>): WebSubmoduleDefinition? {
        return findSection(elements, "Web")?.let { web ->
            return WebSubmoduleDefinition(
                expose = findSection(web.elements, "expose")!!.elements.filterIsInstance<Section>().map {
                    it.name
                },
                serverUrl = web.elements.filterIsInstance<EqualsAssignment>().first {
                    it.name == "serverUrl"
                }.value,
            )
        }
    }

    private fun findSection(elements: List<ParsedElement>, sectionName: String): Section? {
        return elements.find { it is Section && it.name == sectionName } as Section?
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
        val classes: List<ParseComplexStructureResult>,
        val keys: List<KeyDefinition>,
    )
    private fun parseData(elements: List<ParsedElement>): ParsedData {
        return ParsedData(
            classes = parseComplexStructureDefinitions("Data", elements),
            keys = parseKeys("Data", elements)
        )
    }

    private fun parseExternalTypes(elements: List<ParsedElement>): List<String> {
        return findSection(elements, "ExternalTypes")
            ?.elements?.filterIsInstance<Section>()?.map {
                it.name
            } ?: emptyList()
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

        val unknownRootSections = rootSections.map { it.name }.filter { it !in knownRootSections }
        if (unknownRootSections.isNotEmpty()) {
            throw UnknownRootSectionException("Module ${module.value} has unknown root sections: $unknownRootSections")
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
            simple = simple + complex.flatMap { it.inlineSimpleVOs },
            complex = complex.map { it.definition }
        )
    }

    private fun parseSimpleStructureDefinitions(sectionName: String, elements: List<ParsedElement>): List<SimpleStructureDefinition> {
        val voSection = elements.find { it is Section && it.name == sectionName } as Section?
        return voSection?.elements?.filterIsInstance<ColonAssignment>()?.map {
            SimpleStructureDefinition(
                name = it.name,
                typeName = it.value,
                attributes = it.attributes
            )
        } ?: emptyList()
    }

    private fun parseComplexStructureDefinitions(sectionName: String, elements: List<ParsedElement>): List<ParseComplexStructureResult> {
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

    data class ParseComplexStructureResult(
        val definition: ComplexStructureDefinition,
        val inlineSimpleVOs: List<SimpleStructureDefinition>
    )
    private fun parseComplexStructureDefinition(section: Section): ParseComplexStructureResult {
        val inlineSimpleVOs = mutableListOf<SimpleStructureDefinition>()
        val def = ComplexStructureDefinition(
            name = section.name,
            fields = section.elements.filterIsInstance<ColonAssignment>().map {
                if (it.value2 != null) {
                    inlineSimpleVOs.add(SimpleStructureDefinition(it.value, it.value2, emptyList()))
                }
                FieldDefinition(
                    name = it.name,
                    type = parseType(it.value),
                    attributes = it.attributes,
                    defaultValue = it.defaultValue
                )
            }
        )

        return ParseComplexStructureResult(
            definition = def,
            inlineSimpleVOs = inlineSimpleVOs
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
        val complexValueObjects: List<ParseComplexStructureResult>
    )
    private fun parseInterfaces(elements: List<ParsedElement>): ParseInterfaceOutput {
        val interfacesSection = elements.find { it is Section && it.name == "Interfaces" } as Section?

        val valueObjects = mutableListOf<ParseComplexStructureResult>()

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

    private fun parseComplexStructureDefinitions(section: Section?): List<ParseComplexStructureResult> {
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
                .map { it.definition }
            val keys = parseKeys("DataKeys", implSection.elements)
            val data = parseData(implSection.elements)
            return ImplSubmoduleDefinition(
                dataClasses = dataClasses + data.classes.map { it.definition },
                dataKeys = keys + data.keys
            )
        }
    }


}