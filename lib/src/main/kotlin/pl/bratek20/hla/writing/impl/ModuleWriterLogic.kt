package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.writing.api.ModuleWriter

class ModuleWriterLogic(
    private val directories: Directories
): ModuleWriter {

    override fun write(projectPath: Path, generateResult: GenerateResult) {
        val mainPath = projectPath.add(Path("src/main/kotlin/com/some/pkg"))
        val testFixturesPath = projectPath.add(Path("src/testFixtures/kotlin/com/some/pkg"))

        directories.write(mainPath, generateResult.main)
        directories.write(testFixturesPath, generateResult.testFixtures)
    }
}