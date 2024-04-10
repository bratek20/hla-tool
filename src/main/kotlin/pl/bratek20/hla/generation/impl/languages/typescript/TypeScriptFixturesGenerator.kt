package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.FixturesGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade

class TypeScriptFixturesGenerator(module: HlaModule, velocity: VelocityFacade)
    : FixturesGenerator(module, velocity) {

    override fun dirName(): String {
        return "Fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.ts"
    }

    override fun assertsFileName(): String {
        return "Asserts.ts"
    }

    override fun templatesPathPrefix(): String {
        return "templates/typescript"
    }
}