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
        val rootPath = args.getHlaFolderPath().add(args.getProfile().getPaths().getProject())
        val generateResult = args.getGenerateResult()
        val profile = args.getProfile()

        val src = profile.getPaths().getSrc()
        val paths = Paths(
            main = rootPath.add(src.getMain()),
            fixtures = rootPath.add(src.getFixtures()),
            test = rootPath.add(src.getTest())
        )

        writeDirectories(paths, generateResult)

        filesManipulators.manipulate(profile, rootPath, generateResult, args.getOnlyUpdate())

        if (shouldHandleDebug(profile, generateResult.getMain().getName().value)) {
            handleDebug(generateResult)
        }
    }



    private fun writeDirectories(paths: Paths, generateResult: GenerateResult) {
        directories.write(paths.main, generateResult.getMain())
        directories.write(paths.fixtures, generateResult.getFixtures())
        generateResult.getTests()?.let { directories.write(paths.test, it) }
    }

    private fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
        return profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && moduleName.equals("SomeModule", ignoreCase = true)
    }

    private fun handleDebug(generateResult: GenerateResult) {
        val debugPath = Path("../tmp")
        val dirs = DirectoriesLogic()
        dirs.delete(debugPath)
        dirs.write(debugPath, generateResult.getMain())
        dirs.write(debugPath, generateResult.getFixtures())
        generateResult.getTests()?.let { dirs.write(debugPath, it) }
    }

    private data class Paths(val main: Path, val fixtures: Path, val test: Path)
}