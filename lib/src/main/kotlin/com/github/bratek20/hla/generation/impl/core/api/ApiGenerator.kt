package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.patterns.*
import com.github.bratek20.utils.camelToScreamingSnakeCase

class MacrosBuilder: PatternGenerator() {
    //TODO-REF: workaround to not generate file content but loading macros
    override fun generateFileContent(): FileContent? {
        contentBuilder("macros.vm")
            .build()
        return null
    }

    override fun patternName(): PatternName {
        //hack
        return PatternName.DataKeys
    }
}

open class DataClassesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.DataClasses
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

open class PropertyOrDataKeysGenerator(private val data: Boolean): PatternGenerator() {
    override fun patternName(): PatternName {
        return if (data) PatternName.DataKeys else PatternName.PropertyKeys
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

class CustomTypesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.CustomTypes
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

class CustomTypesMapperGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.CustomTypesMapper
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

class SerializedCustomTypesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.SerializedCustomTypes
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

class ApiGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Api
    }

    override fun velocityDirPath(): String {
        return "api"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
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
            EventsGenerator(),
            InterfacesGenerator(),
        )
    }
}