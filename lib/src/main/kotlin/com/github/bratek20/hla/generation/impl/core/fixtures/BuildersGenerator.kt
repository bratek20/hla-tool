package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class BuildersGenerator: FileGenerator() {
    override fun name(): String {
        return "Builders"
    }

    override fun generateFileContent(): FileContent? {
        val defTypes = modules.allComplexStructureDefinitions(module)
        if (defTypes.isEmpty()) {
            return null
        }

        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        val builders = (defTypes).map {
            defTypeFactory.create(apiTypeFactory.create(it))
        }

        return contentBuilder("builders.vm")
            .put("builders", builders)
            .build()
    }
}