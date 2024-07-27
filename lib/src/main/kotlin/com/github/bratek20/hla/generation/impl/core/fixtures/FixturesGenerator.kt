package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class FixturesGenerator: SubmoduleGenerator() {
    override fun name(): String {
        return "Fixtures"
    }

    override fun velocityDirPath(): String {
        return "fixtures"
    }

    override fun getFileGenerators(): List<PatternGenerator> {
        return listOf(
            BuildersGenerator(),
            DiffsGenerator(),
            AssertsGenerator(),
            MocksGenerator(),
        )
    }
}