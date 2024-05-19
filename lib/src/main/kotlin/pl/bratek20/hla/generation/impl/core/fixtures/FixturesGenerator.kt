package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator

class FixturesGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "fixtures"
    }

    override fun velocityDirPath(): String {
        return "fixtures"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            BuildersGenerator(),
            AssertsGenerator()
        )
    }
}