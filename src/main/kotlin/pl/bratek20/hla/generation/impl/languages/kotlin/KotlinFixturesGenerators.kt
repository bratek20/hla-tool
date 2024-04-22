package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator

class KotlinBuildersGenerator(c: ModuleGenerationContext)
    : BuildersGenerator(c) {

    override fun buildersFileName(): String {
        return "Builders.kt"
    }
}

class KotlinAssertsGenerator(c: ModuleGenerationContext)
    : AssertsGenerator(c) {

    override fun assertsFileName(): String {
        return "Asserts.kt"
    }

    override fun assertFunName(voName: String): String {
        return "assert${voName}"
    }
}