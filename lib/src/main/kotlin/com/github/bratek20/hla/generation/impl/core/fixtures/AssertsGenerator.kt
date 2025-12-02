package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class AssertsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Asserts
    }

    data class Asserts(
        val simple: List<ExpectedType<*>>,
        val complex: List<ExpectedType<*>>
    )
    fun doSth(): Asserts? {
        val simpleAssertTypes = modules.allSimpleStructureDefinitions(module)
        var complexAssertTypes = modules.allComplexStructureDefinitions(module)
        if(modules.getGroup(module.getName()).getProfile().getSkipPatterns().contains(PatternName.Events)) {
            val eventStructuresNames =  module.getEvents().map { it.getName() }
            complexAssertTypes = complexAssertTypes.filter { !eventStructuresNames.contains(it.getName()) }
        }
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