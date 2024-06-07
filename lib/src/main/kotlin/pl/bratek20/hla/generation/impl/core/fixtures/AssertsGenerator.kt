package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.FileGenerator

class AssertsGenerator: FileGenerator() {
    override fun name(): String {
        return "Asserts"
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

        val factory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
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