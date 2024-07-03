package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.api.SimpleCustomApiType

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
        if (defTypes.areAllEmpty()) {
            return null
        }

        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        val simpleBuilders = (defTypes.simple).map {
            SimpleBuilder(defTypeFactory.create(apiTypeFactory.create(it)) as SimpleStructureDefType<*>)
        }
        val builders = (defTypes.complex).map {
            defTypeFactory.create(apiTypeFactory.create(it))
        }

        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", builders)
            .build()
    }
}