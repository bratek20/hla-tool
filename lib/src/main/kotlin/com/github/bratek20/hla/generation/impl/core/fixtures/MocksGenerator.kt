package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class MocksGenerator: PatternGenerator() {
    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getInterfaces().isNotEmpty()
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}