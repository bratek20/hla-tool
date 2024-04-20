package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.utils.pascalToCamelCase
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class TypeScriptBuildersGenerator(c: ModulePartGeneratorContext)
    : BuildersGenerator(c, TypeScriptTypes()) {

    override fun buildersFileName(): String {
        return "Builders.ts"
    }

    override fun buildersContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "builders.vm", module.name)
    }
}


class TypeScriptAssertsGenerator(c: ModulePartGeneratorContext)
    : AssertsGenerator(c, TypeScriptTypes()) {

    override fun assertsFileName(): String {
        return "Asserts.ts"
    }

    override fun assertsContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "asserts.vm", module.name)
    }

    override fun assertFunName(voName: String): String {
        return pascalToCamelCase(voName)
    }
}