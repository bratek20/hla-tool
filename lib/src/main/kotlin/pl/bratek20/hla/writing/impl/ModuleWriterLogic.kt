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

        if (profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && profile.getTypeScript() != null && !args.onlyUpdate) {
            val moduleName = generateResult.main.name.value
            updateTsConfigFiles(rootPath, profile.getTypeScript()!!, generateResult)
            updatePackageJson(rootPath, profile.getTypeScript()!!, moduleName)
            updateLaunchJson(rootPath, profile.getTypeScript()!!, moduleName)
        }

        if (shouldHandleDebug(profile, generateResult.main.name.value)) {
            handleDebug(generateResult)
        }
    }

    private fun updateLaunchJson(rootPath: Path, info: TypeScriptInfo, moduleName: String) {
        val path = rootPath.add(info.getLaunchPath())
        files.read(path.add(FileName("launch.json"))).let {
            val currentLines = it.content.lines.toMutableList()
            val startIndex = currentLines.indexOfFirst { it.contains("\"configurations\"") }
            val paddingIndex = currentLines.subList(startIndex, currentLines.size).indexOfLast { it.contains("workspaceFolder") } + startIndex + 2
            val padding = currentLines[paddingIndex].takeWhile { it == ' ' }
            val indexToAdd = paddingIndex + 1
            val newLines = listOf(
                "$padding{",
                "$padding    \"type\": \"node\",",
                "$padding    \"request\": \"launch\",",
                "$padding    \"name\": \"Launch Test App - $moduleName Tests\",",
                "$padding    \"program\": \"\${workspaceFolder}/Test/AFC.testapp.js\",",
                "$padding    \"args\": [\" $moduleName\"],",
                "$padding    \"outFiles\": [",
                "$padding        \"\${workspaceFolder}/**/*.js\"",
                "$padding    ]",
                "$padding},"
            )

            currentLines.addAll(indexToAdd, newLines)

            files.write(path, File(it.name, FileContent(currentLines)))
        }
    }

    private fun updatePackageJson(rootPath: Path, info: TypeScriptInfo, moduleName: String) {
        val path = rootPath.add(info.getPackagePath())
        files.read(path.add(FileName("package.json"))).let {
            val currentLines = it.content.lines.toMutableList()
            val startIndex = currentLines.indexOfFirst { it.contains("\"scripts\"") }
            val indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfLast { it.contains("test ") } + startIndex + 1
            val padding = currentLines[indexToAdd].takeWhile { it == ' ' } + "    "
            val newLines = listOf(
                "$padding\"test $moduleName\": \"npm run build_testapp && npm run run_testapp \\\" $moduleName\\\"\",",
            )

            currentLines.addAll(indexToAdd, newLines)

            files.write(path, File(it.name, FileContent(currentLines)))
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
        files.read(tsconfigPath.add(FileName("tsconfig.json"))).let {
            val currentLines = it.content.lines.toMutableList()

            val startIndex = currentLines.indexOfFirst { it.contains("\"files\"") || it.contains("\"include\"") }
            val indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfFirst { it.contains("]") } + startIndex
            val padding = currentLines[indexToAdd].takeWhile { it == ' ' } + "    "
            val newLines = generateNewLines(directory, prefix, padding)

            currentLines.addAll(indexToAdd, newLines)

            files.write(tsconfigPath, File(it.name, FileContent(currentLines)))
        }
    }

    private fun generateNewLines(directory: Directory, prefix: String, padding: String): List<String> {
        val newLines = mutableListOf<String>()
        extractFiles(directory).forEach { item ->
            newLines.add("")
            item.fileNames.forEach { fileName ->
                newLines.add("$padding\"$prefix${item.submoduleName}/$fileName\",")
            }
        }
        return newLines
    }

    private fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
        return profile.getLanguage() == ModuleLanguage.KOTLIN && moduleName.equals("SomeModule", ignoreCase = true)
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