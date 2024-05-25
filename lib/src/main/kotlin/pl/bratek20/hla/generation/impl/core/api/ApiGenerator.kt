package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.utils.camelToScreamingSnakeCase

class NamedTypesGenerator: FileGenerator() {
    override fun name(): String {
        return "NamedTypes"
    }

    override fun generateFileContent(): FileContent? {
        val namedTypes = module.namedTypes.map { apiTypeFactory.create<NamedApiType>(it) }

        if (namedTypes.isEmpty()) {
            return null
        }

        return contentBuilder("namedTypes.vm")
            .put("namedTypes", namedTypes)
            .build()
    }
}

class ValueObjectsGenerator: FileGenerator() {
    override fun name(): String {
        return "ValueObjects"
    }

    override fun generateFileContent(): FileContent? {
        val valueObjects = module.valueObjects.map { apiTypeFactory.create<ComplexVOApiType>(it) }

        if (valueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("valueObjects", valueObjects)
            .build()
    }
}

class PropertiesOrDataGenerator(private val data: Boolean): FileGenerator() {
    override fun name(): String {
        return if (data) "Data" else "Properties"
    }

    data class SerializableTypeKey(
        val constantName: String,
        val outerKeyType: String,
        val keyName: String,
        val keyType: String
    )

    override fun generateFileContent(): FileContent?{
        if (!data && module.properties.isEmpty()) {
            return null
        }
        if (data && module.data.isEmpty()) {
            return null
        }

        val serializables = if (data) module.data else module.properties
        val keys = if (data) module.dataKeys else module.propertyKeys
        return contentBuilder("serializables.vm")
            .put("serializables", serializables.map {
                apiTypeFactory.create<SerializableApiType>(it)
            })
            .put("keys", keys.map { toApiPropertyKey(it, data) })
            .build()
    }


    private fun toApiPropertyKey(def: KeyDefinition, data: Boolean): SerializableTypeKey {
        val apiType = apiTypeFactory.create(def.type)

        val innerWord = if (data) "Property" else "Property" //TODO change when data keys added in arch

        val outerKeyType: String
        val keyType: String

        if (apiType is ListApiType) {
            outerKeyType = "List${innerWord}Key"
            keyType = apiType.wrappedType.name()
        } else {
            outerKeyType = "Object${innerWord}Key"
            keyType = apiType.name()
        }

        return SerializableTypeKey(
            constantName = camelToScreamingSnakeCase(def.name + "Key"),
            outerKeyType = outerKeyType,
            keyName = def.name,
            keyType = keyType
        )
    }
}

class ExceptionsGenerator: FileGenerator() {
    override fun name(): String {
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
    override fun name(): String {
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
    override fun name(): String {
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
    override fun name(): String {
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
    override fun name(): String {
        return "Api"
    }

    override fun velocityDirPath(): String {
        return "api"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            NamedTypesGenerator(),
            ValueObjectsGenerator(),
            InterfacesGenerator(),
            PropertiesOrDataGenerator(false),
            PropertiesOrDataGenerator(true),
            ExceptionsGenerator(),
            EnumsGenerator(),
            CustomTypesGenerator(),
            CustomTypesMapperGenerator()
        )
    }
}