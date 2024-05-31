package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.FileGenerator

class DiffsGenerator: FileGenerator() {
    override fun name(): String {
        return "Diffs"
    }

    override fun generateFileContent(): FileContent? {
        val simpleAssertTypes = module.namedTypes + module.simpleCustomTypes
        val complexAssertTypes = module.complexCustomTypes + module.properties + module.valueObjects
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

        return contentBuilder("diffs.vm")
            .put("simpleAsserts", simpleAsserts)
            .put("complexAsserts", complexAsserts)
            .build()
    }
}