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
        val rootPath = args.hlaFolderPath.add(args.profile.getProjectPath())
        val generateResult = args.generateResult
        val profile = args.profile

        val fullMainPath = rootPath.add(profile.getMainPath())
        val fullFixturesPath = rootPath.add(profile.getFixturesPath())
        val fullTestPath = rootPath.add(profile.getTestsPath())

        directories.write(fullMainPath, generateResult.main)
        directories.write(fullFixturesPath, generateResult.fixtures)
        if (generateResult.tests != null)
            directories.write(fullTestPath, generateResult.tests)

        //test helping
        val dirs = DirectoriesLogic()
        val moduleName = generateResult.main.name
        if (profile.language == ModuleLanguage.KOTLIN && moduleName.value.lowercase() == "SomeModule".lowercase()) {
            val debugPath = Path("../tmp")
            dirs.deleteDirectory(debugPath)
            dirs.write(debugPath, generateResult.main)
            dirs.write(debugPath, generateResult.fixtures)
            if (generateResult.tests != null)
                dirs.write(debugPath, generateResult.tests)
        }
    }
}