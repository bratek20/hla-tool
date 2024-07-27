package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class FixturesGenerator: SubmoduleGenerator() {
    override fun name(): String {
        return "Fixtures"
    }

    override fun velocityDirPath(): String {
        return "fixtures"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            BuildersGenerator(),
            DiffsGenerator(),
            AssertsGenerator(),
            MocksGenerator(),
        )
    }
}