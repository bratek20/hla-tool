package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.FileContent

class DiffsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Diffs
    }

    override fun generateFileContent(): FileContent? {
        val simpleAssertTypes = modules.allSimpleStructureDefinitions(module)
        val enumTypes = modules.allEnumTypeDefinitions(module)
        val complexAssertTypes = modules.allComplexStructureDefinitions(module)
        if (simpleAssertTypes.isEmpty() && complexAssertTypes.isEmpty() && enumTypes.isEmpty()) {
            return null
        }

        val factory = ExpectedTypeFactory(c)
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