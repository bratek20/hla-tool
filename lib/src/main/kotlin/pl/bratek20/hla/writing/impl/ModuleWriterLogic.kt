package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class ModuleWriterLogic(
    private val directories: Directories
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val projectPath = args.profile.projectPath
        val generateResult = args.generateResult
        val profile = args.profile

        val mainPath = projectPath + profile.srcPath
        val testFixturesPath = projectPath + profile.fixturesPath

        directories.write(Path(mainPath), generateResult.main)
        directories.write(Path(testFixturesPath), generateResult.testFixtures)

        //test helping
        val dirs = DirectoriesLogic()
        val moduleName = generateResult.main.name
        if (profile.language == ModuleLanguage.TYPE_SCRIPT && moduleName.lowercase() == "SomeModule".lowercase()) {
            val debugPath = Path("../tmp")
            dirs.deleteDirectory(debugPath)
            dirs.write(debugPath, generateResult.main)
            dirs.write(debugPath, generateResult.testFixtures)
        }
    }
}