package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.FixturesGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class KotlinFixturesGenerator(module: HlaModule, velocity: VelocityFacade)
    : FixturesGenerator(module, velocity, KotlinTypes()) {

    override fun dirName(): String {
        return "fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.kt"
    }

    override fun buildersContentBuilder(): VelocityFileContentBuilder {
        return kotlinContentBuilder(velocity, "builders.vm", module.name)
    }

    override fun assertsFileName(): String {
        return "Asserts.kt"
    }

    override fun assertsContentBuilder(): VelocityFileContentBuilder {
        return kotlinContentBuilder(velocity, "asserts.vm", module.name)
    }

    override fun assertFunName(voName: String): String {
        return "assert${voName}"
    }
}