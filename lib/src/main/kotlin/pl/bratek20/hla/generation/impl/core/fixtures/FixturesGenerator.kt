package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator

class FixturesGenerator: DirectoryGenerator() {
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
            AssertsGenerator()
        )
    }
}