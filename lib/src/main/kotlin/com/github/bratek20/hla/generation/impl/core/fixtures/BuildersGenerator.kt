package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.codebuilder.*
import com.github.bratek20.hla.codebuilder.Function
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.hla.utils.pascalToCamelCase

class BuildersGenerator: FileGenerator() {
    override fun name(): String {
        return "Builders"
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

    private fun externalTypeBuilder(type: TypeDefinition): CodeBlockBuilder {
        val apiType = apiTypeFactory.create(type) as ExternalApiType
        return Function(
            name = pascalToCamelCase(apiType.rawName),
            args = listOf("value" to apiType.name() + "?"), // TODO soft optional type wrap?
            returnType = apiType.name(),
            body = OneLineBlock("return value!!") // TODO soft optional unpack?
        )
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
                    .add(ManyCodeBlocksSeparatedByLine(
                        externalTypes.map { externalTypeBuilder(it) }
                    ))
                    .build()

        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", builders)
            .put("externalTypesBuilders", externalTypesBuilders)
            .build()
    }
}