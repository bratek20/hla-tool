package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class DiffsGenerator: FileGenerator() {
    override fun name(): String {
        return "Diffs"
    }

    override fun generateFileContent(): FileContent? {
        val simpleAssertTypes = modules.allSimpleStructureDefinitions(module)
        val enumTypes = modules.allEnumTypeDefinitions(module)
        val complexAssertTypes = modules.allComplexStructureDefinitions(module)
        if (simpleAssertTypes.isEmpty() && complexAssertTypes.isEmpty() && enumTypes.isEmpty()) {
            return null
        }

        val factory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
        val simpleAsserts = (simpleAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        } + (enumTypes).map {
            factory.create(apiTypeFactory.create(it))
        }
        val complexAsserts = (complexAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        }

        return contentBuilder("diffs.vm")
            .put("simpleAsserts", simpleAsserts)
            .put("complexAsserts", complexAsserts)
            .build()
    }
}