package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.domain.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.utils.pascalToCamelCase

class TypeScriptBuildersGenerator(c: ModuleGenerationContext)
    : BuildersGenerator(c, TypeScriptTypes()) {

    override fun buildersFileName(): String {
        return "Builders.ts"
    }
}

class TypeScriptAssertsGenerator(c: ModuleGenerationContext)
    : AssertsGenerator(c, TypeScriptTypes()) {

    override fun assertsFileName(): String {
        return "Asserts.ts"
    }

    override fun assertFunName(voName: String): String {
        return pascalToCamelCase(voName)
    }
}