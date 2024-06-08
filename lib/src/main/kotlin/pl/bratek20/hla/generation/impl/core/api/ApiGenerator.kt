package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.utils.camelToScreamingSnakeCase

class ValueObjectsGenerator: FileGenerator() {
    override fun name(): String {
        return "ValueObjects"
    }

    override fun generateFileContent(): FileContent? {
        val simpleValueObjects = module.simpleValueObjects.map { apiTypeFactory.create<SimpleValueObjectApiType>(it) }
        val complexValueObjects = module.complexValueObjects.map { apiTypeFactory.create<ComplexValueObjectApiType>(it) }

        if (complexValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("simpleValueObjects", simpleValueObjects)
            .put("complexValueObjects", complexValueObjects)
            .build()
    }
}

open class DataClassesGenerator: FileGenerator() {
    override fun name(): String {
        return "DataClasses"
    }

    protected open fun dataClasses(): List<ComplexStructureDefinition> {
        return module.dataClasses
    }

    override fun generateFileContent(): FileContent? {
        val dataClasses = dataClasses().map { apiTypeFactory.create<DataClassApiType>(it) }

        if (dataClasses.isEmpty()) {
            return null
        }

        return contentBuilder("dataClasses.vm")
            .put("dataClasses", dataClasses)
            .build()
    }
}

open class PropertyOrDataKeysGenerator(private val data: Boolean): FileGenerator() {
    override fun name(): String {
        return if (data) "DataKeys" else "PropertyKeys"
    }

    data class StorageTypeKey(
        val constantName: String,
        val outerKeyType: String,
        val keyName: String,
        val keyType: String,
        val data: Boolean
    ) {
        fun kotlinPackage(): String {
            return if (data) "pl.bratek20.architecture.data.api" else "pl.bratek20.architecture.properties.api"
        }
    }

    protected open fun dataKeys(): List<KeyDefinition> {
        return module.dataKeys
    }

    override fun generateFileContent(): FileContent?{
        if (!data && module.propertyKeys.isEmpty()) {
            return null
        }
        if (data && dataKeys().isEmpty()) {
            return null
        }

        val keys = if (data) dataKeys() else module.propertyKeys
        return contentBuilder("keys.vm")
            .put("keys", keys.map { toApiPropertyOrDataKey(it, data) })
            .build()
    }


    private fun toApiPropertyOrDataKey(def: KeyDefinition, data: Boolean): StorageTypeKey {
        val apiType = apiTypeFactory.create(def.type)

        val innerWord = if (data) "Data" else "Property"

        val outerKeyType: String
        val keyType: String

        if (apiType is ListApiType) {
            outerKeyType = "List${innerWord}Key"
            keyType = apiType.wrappedType.name()
        } else {
            outerKeyType = "Object${innerWord}Key"
            keyType = apiType.name()
        }

        return StorageTypeKey(
            constantName = camelToScreamingSnakeCase(def.name + "Key"),
            outerKeyType = outerKeyType,
            keyName = def.name,
            keyType = keyType,
            data = data
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

class SerializedCustomTypesGenerator: FileGenerator() {
    override fun name(): String {
        return "SerializedCustomTypes"
    }

    override fun generateFileContent(): FileContent?{
        if (module.complexCustomTypes.isEmpty()) {
            return null
        }

        val complexCustomTypes = module.complexCustomTypes.map { apiTypeFactory.create<ComplexCustomApiType>(it) }

        return contentBuilder("serializedCustomTypes.vm")
            .put("complexCustomTypes", complexCustomTypes)
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
            EnumsGenerator(),
            CustomTypesGenerator(),
            CustomTypesMapperGenerator(),
            SerializedCustomTypesGenerator(),
            ValueObjectsGenerator(),
            DataClassesGenerator(),
            PropertyOrDataKeysGenerator(false),
            PropertyOrDataKeysGenerator(true),
            ExceptionsGenerator(),
            InterfacesGenerator(),
        )
    }
}