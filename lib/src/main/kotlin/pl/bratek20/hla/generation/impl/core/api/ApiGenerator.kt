package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModulePartDirectoryGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.camelToScreamingSnakeCase

class ApiGenerator(
    c: ModuleGenerationContext
): ModulePartDirectoryGenerator(c) {

    override fun generateDirectory(): Directory {
        val files = mutableListOf<File>()
        valueObjectsFile()?.let { files.add(it) }
        interfacesFile()?.let { files.add(it) }
        propertiesFile()?.let { files.add(it) }
        exceptionsFile()?.let { files.add(it) }
        enumsFile()?.let { files.add(it) }
        customTypesFile()?.let { files.add(it) }
        customTypesMapperFile()?.let { files.add(it) }

        return Directory(
            name = language.structure().apiDirName(),
            files = files
        )
    }

    private fun exceptionsFile(): File? {
        val exceptions = module.interfaces
            .flatMap { it.methods }
            .flatMap { it.throws }
            .map { it.name }
            .distinct()

        if (exceptions.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("exceptions.vm")
            .put("exceptions", exceptions)
            .build()

        return File(
            name = language.structure().exceptionsFileName(),
            content = fileContent
        )
    }

    private fun enumsFile(): File? {
        if (module.enums.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("enums.vm")
            .put("enums", module.enums)
            .build()

        return File(
            name = language.structure().enumsFileName(),
            content = fileContent
        )
    }

    private fun toView(value: SimpleStructureDefinition): SimpleVOApiType {
        return apiType(TypeDefinition(value.name, emptyList())) as SimpleVOApiType
    }

    private fun toView(value: ComplexStructureDefinition): ComplexVOApiType {
        return apiType(TypeDefinition(value.name, emptyList())) as ComplexVOApiType
    }


    private fun valueObjectsFile(): File? {
        if (module.simpleValueObjects.isEmpty() && module.complexValueObjects.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("valueObjects.vm")
            .put("valueObjects", ApiValueObjects(
                simpleList = module.simpleValueObjects.map { toView(it) },
                complexList = module.complexValueObjects.map { toView(it) }
            ))
            .build()

        return File(
            name = language.structure().valueObjectsFileName(),
            content = fileContent
        )
    }

    private fun interfacesFile(): File? {
        if (module.interfaces.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("interfaces.vm")
            .put("interfaces", module.interfaces.map { toView(it) })
            .build()

        return File(
            name = language.structure().interfacesFileName(),
            content = fileContent
        )
    }

    private fun propertiesFile(): File? {
        if (module.propertyValueObjects.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("properties.vm")
            .put("properties", module.propertyValueObjects.map {
                apiTypeFactory.create<PropertyApiType>(it)
            })
            .put("keys", module.propertyMappings.map { toApiPropertyKey(it) })
            .build()

        return File(
            name = language.structure().propertiesFileName(),
            content = fileContent
        )
    }

    data class ApiPropertyKey(
        val name: String,
        val value: String
    )
    private fun toApiPropertyKey(mapping: PropertyMapping): ApiPropertyKey {
        val name = camelToScreamingSnakeCase(mapping.key + "Key")
        return ApiPropertyKey(name, mapping.key)
    }

    private fun toView(interf: InterfaceDefinition): InterfaceView {
        return InterfaceView(
            name = interf.name,
            methods = interf.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = toViewType(method.returnType),
                    args = method.args.map { ArgumentView(it.name, toViewType(it.type)) },
                    throws = method.throws.map { it.name }
                )
            }
        )
    }

    private fun toViewType(type: TypeDefinition?): String {
        return apiType(type).name()
    }

    private fun customTypesFile(): File? {
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        val classNames = module.simpleCustomTypes.map { it.name } +
            module.complexCustomTypes.map { it.name }

        val fileContent = contentBuilder("customTypes.vm")
            .put("classNames", classNames)
            .build()

        return File(
            name = language.structure().customTypesFileName(),
            content = fileContent
        )
    }

    private fun toCustomTypeView(value: SimpleStructureDefinition): SimpleCustomApiType {
        return apiType(TypeDefinition(value.name, emptyList())) as SimpleCustomApiType
    }

    private fun toCustomTypeView(value: ComplexStructureDefinition): ComplexCustomApiType {
        return apiType(TypeDefinition(value.name, emptyList())) as ComplexCustomApiType
    }


    private fun customTypesMapperFile(): File? {
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("customTypesMapper.vm")
            .put("customTypes", ApiCustomTypes(
                simpleList = module.simpleCustomTypes.map { toCustomTypeView(it) },
                complexList = module.complexCustomTypes.map { toCustomTypeView(it) }
            ))
            .build()

        return File(
            name = language.structure().customTypesMapperFileName(),
            content = fileContent
        )
    }
}