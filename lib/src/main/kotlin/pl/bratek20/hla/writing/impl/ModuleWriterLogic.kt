package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.HlaProfile
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.facade.api.TypeScriptInfo
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class ModuleWriterLogic(
    private val directories: Directories,
    private val files: Files,
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val rootPath = args.hlaFolderPath.add(args.profile.getProjectPath())
        val generateResult = args.generateResult
        val profile = args.profile

        val paths = Paths(
            main = rootPath.add(profile.getMainPath()),
            fixtures = rootPath.add(profile.getFixturesPath()),
            tests = rootPath.add(profile.getTestsPath())
        )

        writeDirectories(paths, generateResult)

        if (profile.language == ModuleLanguage.TYPE_SCRIPT && profile.typeScript != null) {
            updateTsConfigFiles(rootPath, profile.typeScript, generateResult)
        }

        if (shouldHandleDebug(profile, generateResult.main.name.value)) {
            handleDebug(generateResult)
        }
    }

    private fun writeDirectories(paths: Paths, generateResult: GenerateResult) {
        directories.write(paths.main, generateResult.main)
        directories.write(paths.fixtures, generateResult.fixtures)
        generateResult.tests?.let { directories.write(paths.tests, it) }
    }

    private fun updateTsConfigFiles(rootPath: Path, info: TypeScriptInfo, generateResult: GenerateResult) {
        val typeScriptPaths = TypeScriptPaths(
            mainTsconfig = rootPath.add(info.getMainTsconfigPath()),
            testTsconfig = rootPath.add(info.getTestTsconfigPath())
        )

        updateTsConfigFile(typeScriptPaths.mainTsconfig, generateResult.main, "Src/${generateResult.main.name.value}/")

        var fixturesAndTestDir = generateResult.fixtures.copy()
        if (generateResult.tests != null) {
            fixturesAndTestDir = Directory(
                name = generateResult.fixtures.name,
                directories = generateResult.fixtures.directories + generateResult.tests.directories
            )
        }
        updateTsConfigFile(typeScriptPaths.testTsconfig, fixturesAndTestDir, "${generateResult.main.name.value}/")
    }

    private fun updateTsConfigFile(tsconfigPath: Path, directory: Directory, prefix: String) {
        files.read(tsconfigPath).let {
            val currentLines = it.content.lines.toMutableList()
            val newLines = generateNewLines(directory, prefix)
            currentLines.addAll(2, newLines)
            files.write(tsconfigPath, File(it.name, FileContent(currentLines)))
        }
    }

    private fun generateNewLines(directory: Directory, prefix: String): List<String> {
        val newLines = mutableListOf<String>()
        extractFiles(directory).forEach { item ->
            item.fileNames.forEach { fileName ->
                newLines.add("    \"$prefix${item.submoduleName}/$fileName\",")
            }
            newLines.add("")
        }
        newLines.removeAt(newLines.size - 1)
        return newLines
    }

    private fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
        return profile.language == ModuleLanguage.KOTLIN && moduleName.equals("SomeModule", ignoreCase = true)
    }

    private fun handleDebug(generateResult: GenerateResult) {
        val debugPath = Path("../tmp")
        val dirs = DirectoriesLogic()
        dirs.delete(debugPath)
        dirs.write(debugPath, generateResult.main)
        dirs.write(debugPath, generateResult.fixtures)
        generateResult.tests?.let { dirs.write(debugPath, it) }
    }

    data class Paths(val main: Path, val fixtures: Path, val tests: Path)
    data class TypeScriptPaths(val mainTsconfig: Path, val testTsconfig: Path)
    data class ExtractedFile(val submoduleName: String, val fileNames: List<String>)

    private fun extractFiles(dir: Directory): List<ExtractedFile> {
        return dir.directories.map { subDir ->
            ExtractedFile(subDir.name.value, subDir.files.map { it.name.value })
        }
    }
}