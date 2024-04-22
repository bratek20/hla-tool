package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.generation.impl.core.language.LanguageStrategy

class FixturesGenerator(
    private val c: ModuleGenerationContext,
) {
    fun generateDirectory(): Directory {
        val buildersFile = BuildersGenerator(c).generateFile()
        val assertsFile = AssertsGenerator(c).generateFile()

        return Directory(
            name = c.language.structure().fixturesDirName(),
            files = listOf(
                buildersFile,
                assertsFile
            )
        )
    }
}