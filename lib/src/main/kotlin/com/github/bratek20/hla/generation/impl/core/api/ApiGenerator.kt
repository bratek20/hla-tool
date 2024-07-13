package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.utils.camelToScreamingSnakeCase

class MacrosBuilder: FileGenerator() {
    override fun name(): String {
        return "Macros"
    }

    //TODO-REF: workaround to not generate file content but loading macros
    override fun generateFileContent(): FileContent? {
        contentBuilder("macros.vm")
            .build()
        return null
    }
}

class ValueObjectsGenerator: FileGenerator() {
    override fun name(): String {
        return "ValueObjects"
    }

    override fun generateFileContent(): FileContent? {
        val simpleValueObjects = module.getSimpleValueObjects().map { apiTypeFactory.create<SimpleValueObjectApiType>(it) }
        val complexValueObjects = module.getComplexValueObjects().map { apiTypeFactory.create<ComplexValueObjectApiType>(it) }

        if (simpleValueObjects.isEmpty() && complexValueObjects.isEmpty()) {
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
        return module.getDataClasses()
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
            return if (data) "com.github.bratek20.architecture.data.api" else "com.github.bratek20.architecture.properties.api"
        }
    }

    protected open fun dataKeys(): List<KeyDefinition> {
        return module.getDataKeys()
    }

    override fun generateFileContent(): FileContent?{
        if (!data && module.getPropertyKeys().isEmpty()) {
            return null
        }
        if (data && dataKeys().isEmpty()) {
            return null
        }

        val keys = if (data) dataKeys() else module.getPropertyKeys()
        return contentBuilder("keys.vm")
            .put("keys", keys.map { toApiPropertyOrDataKey(it, data) })
            .build()
    }


    private fun toApiPropertyOrDataKey(def: KeyDefinition, data: Boolean): StorageTypeKey {
        val apiType = apiTypeFactory.create(def.getType())

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
            constantName = camelToScreamingSnakeCase(def.getName() + "${innerWord}Key"),
            outerKeyType = outerKeyType,
            keyName = def.getName(),
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
        val exceptions = module.getInterfaces()
            .flatMap { it.getMethods() }
            .flatMap { it.getThrows() }
            .map { it.getName() }
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
        if (module.getEnums().isEmpty()) {
            return null
        }

        return contentBuilder("enums.vm")
            .put("enums", module.getEnums())
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
        if (module.getSimpleCustomTypes().isEmpty() && module.getComplexCustomTypes().isEmpty()) {
            return null
        }

        val classNames = module.getSimpleCustomTypes().map { it.getName() } +
            module.getComplexCustomTypes().map { it.getName() }

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
        if (module.getSimpleCustomTypes().isEmpty() && module.getComplexCustomTypes().isEmpty()) {
            return null
        }

        return contentBuilder("customTypesMapper.vm")
            .put("customTypes", ApiCustomTypes(
                simpleList = module.getSimpleCustomTypes().map { apiTypeFactory.create(it) },
                complexList = module.getComplexCustomTypes().map { apiTypeFactory.create(it) }
            ))
            .build()
    }
}

class SerializedCustomTypesGenerator: FileGenerator() {
    override fun name(): String {
        return "SerializedCustomTypes"
    }

    override fun generateFileContent(): FileContent?{
        if (module.getComplexCustomTypes().isEmpty()) {
            return null
        }

        val complexCustomTypes = module.getComplexCustomTypes().map { apiTypeFactory.create<ComplexCustomApiType>(it) }

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

class MacrosGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Macros"
    }

    override fun velocityDirPath(): String {
        return "macros"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            MacrosBuilder()
        )
    }
}