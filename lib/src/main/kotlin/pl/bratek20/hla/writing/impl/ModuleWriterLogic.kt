package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs
import pl.bratek20.utils.logs.api.Logger

class ModuleWriterLogic(
    private val directories: Directories,
    private val logger: Logger
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val projectPath = args.profile.getProjectPath()
        val generateResult = args.generateResult
        val profile = args.profile

        val fullMainPath = projectPath.add(profile.getMainPath())
        val fullFixturesPath = projectPath.add(profile.getFixturesPath())

        logger.info("Writing to: $fullMainPath")
        directories.write(fullMainPath, generateResult.main)

        logger.info("Writing to: $fullFixturesPath")
        directories.write(fullFixturesPath, generateResult.fixtures)

        //test helping
        val dirs = DirectoriesLogic()
        val moduleName = generateResult.main.name
        if (profile.language == ModuleLanguage.KOTLIN && moduleName.value.lowercase() == "SomeModule".lowercase()) {
            val debugPath = Path("../tmp")
            dirs.deleteDirectory(debugPath)
            dirs.write(debugPath, generateResult.main)
            dirs.write(debugPath, generateResult.fixtures)
        }
    }
}