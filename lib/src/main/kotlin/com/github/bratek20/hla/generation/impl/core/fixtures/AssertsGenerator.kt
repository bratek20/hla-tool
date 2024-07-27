package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class AssertsGenerator: PatternGenerator() {
    override fun name(): String {
        return "Asserts"
    }

    override fun patternName(): PatternName {
        return PatternName.Asserts
    }

    data class Asserts(
        val simple: List<ExpectedType<*>>,
        val complex: List<ExpectedType<*>>
    )
    fun doSth(): Asserts? {
        val simpleAssertTypes = modules.allSimpleStructureDefinitions(module)
        val complexAssertTypes = modules.allComplexStructureDefinitions(module)
        if (simpleAssertTypes.isEmpty() && complexAssertTypes.isEmpty()) {
            return null
        }

        val factory = ExpectedTypeFactory(c)
        val simpleAsserts = (simpleAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        }
        val complexAsserts = (complexAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        }

        return Asserts(simpleAsserts, complexAsserts)
    }

    override fun generateFileContent(): FileContent? {
        val (simpleAsserts, complexAsserts) = doSth() ?: return null

        return contentBuilder("asserts.vm")
            .put("simpleAsserts", simpleAsserts)
            .put("complexAsserts", complexAsserts)
            .build()
    }
}