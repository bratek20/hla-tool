package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.FunctionBuilder
import com.github.bratek20.codebuilder.builders.function
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

class BuildersGenerator: PatternGenerator() {
    override fun name(): String {
        return "Builders"
    }

    override fun patternName(): PatternName {
        return PatternName.Builders
    }

    data class SimpleBuilder(
        val def: SimpleStructureDefType<*>
    ) {
        // used by velocity
        fun declaration(): String {
            return "${def.funName()}(value: ${def.name()} = ${def.defaultValue()}): ${def.api.name()}"
        }

        // used by velocity
        fun body(): String {
            return "return ${def.api.constructorCall()}(value)"
        }
    }

    private fun externalTypeBuilder(type: TypeDefinition): FunctionBuilder {
        val apiType = apiTypeFactory.create(type) as ExternalApiType
        return function {
            name = pascalToCamelCase(apiType.rawName)
            addArg {
                name = "value"
                this.type = type(apiType.name() + "?") //TODO soft optional type wrap?
            }
            returnType = type(apiType.name())
            body = {
                line("return value!!") // TODO soft optional unpack?
            }
        }
    }

    override fun generateFileContent(): FileContent? {
        val defTypes = modules.allStructureDefinitions(module)
        val externalTypes = modules.allExternalTypesDefinitions(module)
        if (defTypes.areAllEmpty() && externalTypes.isEmpty()) {
            return null
        }

        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        val simpleBuilders = (defTypes.simple).map {
            SimpleBuilder(defTypeFactory.create(apiTypeFactory.create(it)) as SimpleStructureDefType<*>)
        }
        val builders = (defTypes.complex).map {
            defTypeFactory.create(apiTypeFactory.create(it))
        }

        val externalTypesBuilders = if (externalTypes.isEmpty())
                null
            else
                CodeBuilder(lang)
                    .addMany(
                        externalTypes.map { externalTypeBuilder(it) }
                    )
                    .build()


        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", builders)
            .put("externalTypesBuilders", externalTypesBuilders)
            .build()
    }
}