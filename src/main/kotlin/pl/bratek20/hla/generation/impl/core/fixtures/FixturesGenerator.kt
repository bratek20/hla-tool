package pl.bratek20.hla.generation.impl.core.fixtures

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.generation.impl.core.language.LanguageStrategy

class FixturesGenerator(
    private val languageStrategy: LanguageStrategy,
) {
    fun generateDirectory(): Directory {
        val buildersFile = languageStrategy.buildersGenerator().generateFile()
        val assertsFile = languageStrategy.assertsGenerator().generateFile()

        return Directory(
            name = languageStrategy.fixturesDirName(),
            files = listOf(
                buildersFile,
                assertsFile
            )
        )
    }
}