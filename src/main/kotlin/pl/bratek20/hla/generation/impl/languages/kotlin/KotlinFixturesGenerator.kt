package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.FixturesGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade

class KotlinFixturesGenerator(module: HlaModule, velocity: VelocityFacade)
    : FixturesGenerator(module, velocity) {

    override fun dirName(): String {
        return "fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.kt"
    }

    override fun assertsFileName(): String {
        return "Asserts.kt"
    }

    override fun templatesPathPrefix(): String {
        return "templates/kotlin"
    }
}