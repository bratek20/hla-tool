package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class FixturesGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Fixtures
    }

    override fun velocityDirPath(): String {
        return "fixtures"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            BuildersGenerator(),
            DiffsGenerator(),
            AssertsGenerator(),
            MocksGenerator(),
        )
    }
}