package com.github.bratek20.hla.generation.impl.core.tests

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode

class ImplTestGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ImplTest
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent? {
        return contentBuilder("implTest.vm")
            .build()
    }
}

class TestsGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Tests
    }

    override fun velocityDirPath(): String {
        return "tests"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            ImplTestGenerator(),
        )
    }
}