package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.codebuilder.CodeBuilder
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

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

        val legacy = CodeBuilder()
            .line("fun legacyType(value: com.some.pkg.legacy.LegacyType?): com.some.pkg.legacy.LegacyType {")
            .tab()
            .line("return value!!")
            .untab()
            .line("}")
            .build()

        val externalTypesBuilders = if (externalTypes.isEmpty())
                null
            else
                legacy

        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", builders)
            .put("externalTypesBuilders", externalTypesBuilders)
            .build()
    }
}