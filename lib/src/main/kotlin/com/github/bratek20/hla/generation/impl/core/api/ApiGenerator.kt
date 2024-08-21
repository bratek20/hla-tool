package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.codebuilder.builders.classBlock
import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.builders.enum
import com.github.bratek20.codebuilder.builders.field
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.kotlin.kotlinFile
import com.github.bratek20.codebuilder.ops.string
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.languages.kotlin.profileToRootPackage
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

class ValueObjectsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ValueObjects
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

class ExceptionsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Exceptions
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.KOTLIN
    }

    override fun shouldGenerate(): Boolean {
        return modules.allExceptionNamesForCurrent().isNotEmpty()
    }

    override fun applyOperations(cb: CodeBuilder) {
        cb.kotlinFile {
            packageName = submodulePackage(SubmoduleName.Api, c)

            addImport("com.github.bratek20.architecture.exceptions.ApiException")

            modules.allExceptionNamesForCurrent().forEach {
                addClass {
                    name = it
                    extends = "ApiException"
                    constructor {
                        addArg {
                            name = "message"
                            type = baseType(BaseType.STRING)
                            defaultValue = "\"\""
                        }
                    }
                }
            }
        }
    }

    override fun generateFileContent(): FileContent?{
        val exceptions = modules.allExceptionNamesForCurrent()

        if (exceptions.isEmpty()) {
            return null
        }

        return contentBuilder("exceptions.vm")
            .put("exceptions", exceptions)
            .build()
    }
}

fun submodulePackage(submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return profileToRootPackage(c.domain.profile) + "." + c.module.getName().value.lowercase() + "." + submodule.name.lowercase()
}

class EnumsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Enums
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getEnums().isNotEmpty()
    }

    override fun applyOperations(cb: CodeBuilder) {
        if (language.name() == ModuleLanguage.KOTLIN) {
            cb.kotlinFile {
                packageName = submodulePackage(SubmoduleName.Api, c)
                module.getEnums().forEach {
                    addEnum {
                        name = it.getName()
                        it.getValues().forEach { addValue(it) }
                    }
                }
            }
        }
        if (language.name() == ModuleLanguage.TYPE_SCRIPT) {
            cb.add {
                module.getEnums().forEach {
                    val enumName = it.getName()
                    classBlock {
                        name = enumName
                        extends = "StringEnumClass"
                        it.getValues().forEach {
                            addField {
                                name = it
                                static = true
                                value = { constructorCall {
                                    className = enumName
                                    addArg {
                                        string(it)
                                    }
                                } }
                            }
                        }
                    }
                }
            }
        }
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
            InterfacesGenerator(),
        )
    }
}