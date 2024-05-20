package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.FileGenerator

class AssertsGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Asserts"
    }

    override fun generateFileContent(): FileContent? {
        val assertTypes = module.complexCustomTypes + module.properties + module.valueObjects
        if (assertTypes.isEmpty()) {
            return null
        }

        val factory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
        val asserts = (assertTypes).map {
            factory.create(apiTypeFactory.create(it))
        }

        return contentBuilder("asserts.vm")
            .put("asserts", asserts)
            .build()
    }
}