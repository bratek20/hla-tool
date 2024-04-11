package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.FixturesGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class TypeScriptFixturesGenerator(module: HlaModule, velocity: VelocityFacade)
    : FixturesGenerator(module, velocity, TypeScriptTypes()) {

    override fun dirName(): String {
        return "Fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.ts"
    }

    override fun buildersContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "builders.vm", module.name)
    }

    override fun assertsFileName(): String {
        return "Asserts.ts"
    }

    override fun assertsContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "asserts.vm", module.name)
    }
}