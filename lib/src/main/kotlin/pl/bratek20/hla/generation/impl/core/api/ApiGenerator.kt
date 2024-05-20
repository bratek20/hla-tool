package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.utils.camelToScreamingSnakeCase

class NamedTypesGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "NamedTypes"
    }

    override fun generateFileContent(): FileContent? {
        val namedTypes = module.simpleValueObjects.map { apiTypeFactory.create<SimpleVOApiType>(it) }

        if (namedTypes.isEmpty()) {
            return null
        }

        return contentBuilder("namedTypes.vm")
            .put("namedTypes", namedTypes)
            .build()
    }
}

class ValueObjectsGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "ValueObjects"
    }

    override fun generateFileContent(): FileContent? {
        val valueObjects = module.complexValueObjects.map { apiTypeFactory.create<ComplexVOApiType>(it) }

        if (valueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("valueObjects", valueObjects)
            .build()
    }
}

class PropertiesGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Properties"
    }

    data class ApiPropertyKey(
        val name: String,
        val value: String
    )

    override fun generateFileContent(): FileContent?{
        if (module.propertyValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("properties.vm")
            .put("properties", module.propertyValueObjects.map {
                apiTypeFactory.create<PropertyApiType>(it)
            })
            .put("keys", module.propertyMappings.map { toApiPropertyKey(it) })
            .build()
    }


    private fun toApiPropertyKey(mapping: PropertyMapping): ApiPropertyKey {
        val name = camelToScreamingSnakeCase(mapping.key + "Key")
        return ApiPropertyKey(name, mapping.key)
    }
}

class ExceptionsGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Exceptions"
    }

    override fun generateFileContent(): FileContent?{
        val exceptions = module.interfaces
            .flatMap { it.methods }
            .flatMap { it.throws }
            .map { it.name }
            .distinct()

        if (exceptions.isEmpty()) {
            return null
        }

        return contentBuilder("exceptions.vm")
            .put("exceptions", exceptions)
            .build()
    }
}

class EnumsGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Enums"
    }

    override fun generateFileContent(): FileContent?{
        if (module.enums.isEmpty()) {
            return null
        }

        return contentBuilder("enums.vm")
            .put("enums", module.enums)
            .build()
    }
}

class CustomTypesGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "CustomTypes"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent?{
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        val classNames = module.simpleCustomTypes.map { it.name } +
            module.complexCustomTypes.map { it.name }

        return contentBuilder("customTypes.vm")
            .put("classNames", classNames)
            .build()
    }
}

class CustomTypesMapperGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "CustomTypesMapper"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent?{
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        return contentBuilder("customTypesMapper.vm")
            .put("customTypes", ApiCustomTypes(
                simpleList = module.simpleCustomTypes.map { apiTypeFactory.create(it) },
                complexList = module.complexCustomTypes.map { apiTypeFactory.create(it) }
            ))
            .build()
    }
}

class ApiGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "api"
    }

    override fun velocityDirPath(): String {
        return "api"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            NamedTypesGenerator(),
            ValueObjectsGenerator(),
            InterfacesGenerator(),
            PropertiesGenerator(),
            ExceptionsGenerator(),
            EnumsGenerator(),
            CustomTypesGenerator(),
            CustomTypesMapperGenerator()
        )
    }
}