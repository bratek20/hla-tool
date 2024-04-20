package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class KotlinBuildersGenerator(c: ModulePartGeneratorContext)
    : BuildersGenerator(c, KotlinTypes()) {

    override fun buildersFileName(): String {
        return "Builders.kt"
    }

    override fun buildersContentBuilder(): VelocityFileContentBuilder {
        return kotlinContentBuilder(velocity, "builders.vm", module.name)
    }
}

class KotlinAssertsGenerator(c: ModulePartGeneratorContext)
    : AssertsGenerator(c, KotlinTypes()) {

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