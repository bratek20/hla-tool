package com.github.bratek20.hla.generation.impl.core.examples.patterns

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.BaseApiType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.*

abstract class ExampleJsonLogic {
   companion object {
       private val serializer = SerializationFactory.createSerializer(
           SerializerConfig.create(
               readable = true
           )
       )
   }

    fun createFile(): File {
        return File.create(
            name = FileName("${getName()}.json"),
            content = FileContent.fromString(createExampleJson())
        )
    }
    protected abstract fun createExampleJson(): String
    protected abstract fun getName(): String
    protected fun anyToJson(example: Any): String {
        return serializer.serialize(example).getValue()
    }
}

class ExampleKeyDefinitionLogic(
    private val def: KeyDefinition,
    private val apiTypeFactory: ApiTypeFactory,
): ExampleJsonLogic() {

    override fun createExampleJson(): String {
        val apiType = apiTypeFactory.create(def.getType())
        val example = apiType.getExample()
        return anyToJson(example)
    }

    override fun getName(): String {
        return def.getName()
    }
}

class ExampleInterfaceMethodLogic(
    private val def: MethodDefinition,
    private val exposedName: String,
    private val apiTypeFactory: ApiTypeFactory,
): ExampleJsonLogic() {

    override fun createExampleJson(): String {
        val valuesMap = mutableMapOf<String, Any>()
        valuesMap["input"] = "No input for this method"

        if(def.getArgs().isNotEmpty()) {
            if(def.getArgs().size == 1){
                valuesMap["input"] = def.getArgs().first().let { arg ->
                    val apiType = apiTypeFactory.create(arg.getType())
                    apiType.getExample()
                }
            } else {
                valuesMap["input"] = def.getArgs().map { arg ->
                    val apiType = apiTypeFactory.create(arg.getType())
                    apiType.getExample()
                }
            }

        }

        valuesMap["output"] = "No output for this method"
        val returnApiType = apiTypeFactory.create(def.getReturnType())
        if(!BaseApiType.isVoid(returnApiType)) {
            valuesMap["output"] = returnApiType.getExample()
        }

        valuesMap["exceptions"] = "No exceptions for this method"
        if(def.getThrows().isNotEmpty()) {
            valuesMap["exceptions"] = def.getThrows().map { exception ->
                exception.getName()
            }
        }
        return anyToJson(valuesMap)
    }

    override fun getName(): String {
        return "${exposedName}.${def.getName()}"
    }

}

class HandlersExamplesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.HandlersExamples
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT && getExposedInterfaces().isNotEmpty()
    }

    override fun getDirectory(): Directory? {
        val exposedInterfaces = getExposedInterfaces()
        if (exposedInterfaces.isEmpty()) {
            return null
        }

        val exampleLogics = exposedInterfaces.flatMap { exposed ->
            val interfaceDef = c.module.getInterfaces().first { it.getName() == exposed.getName() }
            interfaceDef.getMethods().map { method ->
                ExampleInterfaceMethodLogic(method, exposed.getExposedName(), c.apiTypeFactory)
            }
        }

        return Directory.create(
            name = DirectoryName("Handlers"),
            files = exampleLogics.map { it.createFile() }
        )
    }

    private fun getExposedInterfaces(): List<ExposedInterface> {
        val web = c.module.getWebSubmodule() ?: return emptyList()
        val handlers = web.getPlayFabHandlers() ?: return emptyList()
        return handlers.getExposedInterfaces()
    }
}

abstract class KeyExamplesGenerator: PatternGenerator() {

    protected abstract fun getKeys(): List<KeyDefinition>
    protected abstract fun getDirectoryName(): DirectoryName
    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT && getKeys().isNotEmpty()
    }

    override fun getDirectory(): Directory? {
        return getDirectoryForKeysWithName(getKeys(), getDirectoryName())
    }

    private fun getDirectoryForKeysWithName(keys: List<KeyDefinition>, directoryName: DirectoryName): Directory? {
        val keysExamplesLogic = keys.map {
            ExampleKeyDefinitionLogic(it, apiTypeFactory)
        }
        if (keysExamplesLogic.isEmpty()) {
            return null
        }
        return Directory.create(
            name = directoryName,
            files = keysExamplesLogic.map {it.createFile()}
        )
    }

}

class TitleDataExamplesGenerator: KeyExamplesGenerator() {
    override fun getKeys(): List<KeyDefinition> {
        return module.getPropertyKeys()
    }

    override fun getDirectoryName(): DirectoryName {
        return DirectoryName("TitleData")
    }

    override fun patternName(): PatternName {
        return PatternName.TitleDataExamples
    }
}

class PlayerDataExamplesGenerator: KeyExamplesGenerator() {
    override fun getKeys(): List<KeyDefinition> {
        return module.getDataKeys()
    }

    override fun getDirectoryName(): DirectoryName {
        return DirectoryName("PlayerData")
    }

    override fun patternName(): PatternName {
        return PatternName.PlayerDataExamples
    }
}

