package com.github.bratek20.hla.examples.impl

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.BaseApiType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

enum class ExampleType {
    PROPERTY,
    DATA,
    INTERFACE
}

class ExampleKeyDefinitionLogic(
    private val def: KeyDefinition,
    private val apiTypeFactory: ApiTypeFactory,
    exampleType: ExampleType
): ExampleJsonLogic(exampleType) {

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
    private val apiTypeFactory: ApiTypeFactory,
    exampleType: ExampleType
): ExampleJsonLogic(exampleType) {
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
        return def.getName()
    }

}

abstract class ExampleJsonLogic(val exampleType: ExampleType) {
    abstract fun createExampleJson(): String
    abstract fun getName(): String
    fun anyToJson(example: Any): String {
        val serializer = SerializationFactory.createSerializer(
            SerializerConfig.create(
                readable = true
            )
        )
        return serializer.serialize(example).getValue()
    }
}

class ExampleGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Examples
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        val exposedInterfaces = getExposedInterfaces(c.module.getWebSubmodule())
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT &&
                (c.module.getPropertyKeys().isNotEmpty() || c.module.getDataKeys().isNotEmpty() || !exposedInterfaces.isNullOrEmpty())
    }

    override fun getFiles(): List<File> {
        val exampleLogics = createExampleLogics(c.module, c.apiTypeFactory)
        return exampleLogics.map {
            val filePrefix = when (it.exampleType) {
                ExampleType.DATA -> "PD."
                ExampleType.PROPERTY -> "TD."
                ExampleType.INTERFACE -> "HANDLER.${c.module.getName()}."
            }
            File.create(
                name = FileName("$filePrefix${it.getName()}.json"),
                content = FileContent.fromString(it.createExampleJson())
            )
        }
    }
}
fun createExampleLogics(module: ModuleDefinition, apiTypeFactory: ApiTypeFactory): List<ExampleJsonLogic> {
    return module.getDataKeys().map {
        ExampleKeyDefinitionLogic(it, apiTypeFactory, ExampleType.DATA)
    } + module.getPropertyKeys().map {
        ExampleKeyDefinitionLogic(it, apiTypeFactory, ExampleType.PROPERTY)
    } + createExampleInterfaceMethodLogic(module, apiTypeFactory)
}

fun getExposedInterfaces(web: WebSubmoduleDefinition?): List<ExposedInterface> {
    if(web == null) {
        return emptyList()
    }
    val handlers = web.getPlayFabHandlers() ?: return emptyList()
    return handlers.getExposedInterfaces()
}

fun createExampleInterfaceMethodLogic(module: ModuleDefinition, apiTypeFactory: ApiTypeFactory): List<ExampleInterfaceMethodLogic> {
    val exposedInterfacesNames = getExposedInterfaces(module.getWebSubmodule()).map { it.getName() }
    val interfacesToMap = module.getInterfaces().filter { exposedInterfacesNames.contains(it.getName()) }
    val interfacesMethodsLogic = mutableListOf<ExampleInterfaceMethodLogic>()
    interfacesToMap.forEach { interfaceToMap ->
        interfaceToMap.getMethods().map {
            interfacesMethodsLogic.add(ExampleInterfaceMethodLogic(it, apiTypeFactory, ExampleType.INTERFACE))
        }
    }

    return interfacesMethodsLogic
}