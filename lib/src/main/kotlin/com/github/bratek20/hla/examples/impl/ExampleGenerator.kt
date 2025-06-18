package com.github.bratek20.hla.examples.impl

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.architecture.structs.api.Struct
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.SerializableApiType
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

fun KeyDefinition.isList(): Boolean {
    return this.getType().getWrappers().contains(TypeWrapper.LIST)
}

class ExampleJsonLogic(
    private val def: KeyDefinition,
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
) {

    fun createExampleJson(): String {
        val apiType = apiTypeFactory.create(def.getType())
        val example = apiType.getExample()
        return anyToJson(example)
    }

    private fun anyToJson(example: Any): String {
        val serializer = SerializationFactory.createSerializer(
            SerializerConfig.create(
                readable = true
            )
        )
        return serializer.serialize(example).getValue()
    }

    fun getPropertyName(): String {
        return def.getName()
    }
}

class ExampleGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Examples
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT &&
                c.module.getPropertyKeys().isNotEmpty()
    }

    override fun getFiles(): List<File> {
        val exampleLogics = createExampleLogics(c.module, c.apiTypeFactory, c.typesWorldApi)
        return exampleLogics.map {
            File.create(
                name = FileName("TD${it.getPropertyName()}.json"),
                content = FileContent.fromString(it.createExampleJson())
            )
        }
    }
}
fun createExampleLogics(module: ModuleDefinition, apiTypeFactory: ApiTypeFactory, typesWorldApi: TypesWorldApi): List<ExampleJsonLogic> {
    val createExampleLogic = { def: KeyDefinition ->
        ExampleJsonLogic(def, apiTypeFactory, typesWorldApi)
    }
    return module.getPropertyKeys().map {
        createExampleLogic(it)
    }
}