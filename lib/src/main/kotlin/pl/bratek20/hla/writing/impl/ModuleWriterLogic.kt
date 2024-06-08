package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.HlaProfile
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.facade.api.TypeScriptConfig
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class ModuleWriterLogic(
    private val directories: Directories,
    private val filesManipulators: FilesManipulators
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val rootPath = args.hlaFolderPath.add(args.profile.getPaths().getProject())
        val generateResult = args.generateResult
        val profile = args.profile

        val src = profile.getPaths().getSrc()
        val paths = Paths(
            main = rootPath.add(src.getMain()),
            fixtures = rootPath.add(src.getFixtures()),
            test = rootPath.add(src.getTest())
        )

        writeDirectories(paths, generateResult)

        filesManipulators.manipulate(profile, rootPath, generateResult, args.onlyUpdate)

        if (shouldHandleDebug(profile, generateResult.main.name.value)) {
            handleDebug(generateResult)
        }
    }



    private fun writeDirectories(paths: Paths, generateResult: GenerateResult) {
        directories.write(paths.main, generateResult.main)
        directories.write(paths.fixtures, generateResult.fixtures)
        generateResult.tests?.let { directories.write(paths.test, it) }
    }

    private fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
        return profile.getLanguage() == ModuleLanguage.KOTLIN && moduleName.equals("TypesModule", ignoreCase = true)
    }

    private fun handleDebug(generateResult: GenerateResult) {
        val debugPath = Path("../tmp")
        val dirs = DirectoriesLogic()
        dirs.delete(debugPath)
        dirs.write(debugPath, generateResult.main)
        dirs.write(debugPath, generateResult.fixtures)
        generateResult.tests?.let { dirs.write(debugPath, it) }
    }

    private data class Paths(val main: Path, val fixtures: Path, val test: Path)
}