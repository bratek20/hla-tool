package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.facade.api.HlaProfile
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.facade.api.TypeScriptConfig
import pl.bratek20.hla.generation.api.GenerateResult

class FilesManipulators(
    private val files: Files,
) {
    fun manipulate(profile: HlaProfile, rootPath: Path, generateResult: GenerateResult, onlyUpdate: Boolean) {
        if (profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && profile.getTypeScript() != null && !onlyUpdate) {
            val moduleName = generateResult.getMain().getName().value
            updateTsConfigFiles(rootPath, profile.getTypeScript()!!, generateResult, profile)
            updatePackageJson(rootPath, profile.getTypeScript()!!, moduleName)
            updateLaunchJson(rootPath, profile.getTypeScript()!!, moduleName)
        }
    }

    private fun updateLaunchJson(rootPath: Path, info: TypeScriptConfig, moduleName: String) {
        val path = rootPath.add(info.getLaunchJsonPath())
        files.read(path.add(FileName("launch.json"))).let {
            val currentLines = it.getContent().lines.toMutableList()
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

            if (currentLines.any { it.contains("Launch Test App - $moduleName Tests") }) {
                return
            }

            currentLines.addAll(indexToAdd, newLines)

            files.write(path, File.create(it.getName(), FileContent(currentLines)))
        }
    }

    private fun updatePackageJson(rootPath: Path, info: TypeScriptConfig, moduleName: String) {
        val path = rootPath.add(info.getPackageJsonPath())
        files.read(path.add(FileName("package.json"))).let {
            val currentLines = it.getContent().lines.toMutableList()
            val startIndex = currentLines.indexOfFirst { it.contains("\"scripts\"") }
            val indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfLast { it.contains("test ") } + startIndex + 1
            val padding = currentLines[indexToAdd].takeWhile { it == ' ' } + "    "
            val newLines = listOf(
                "$padding\"test $moduleName\": \"npm run build_testapp && npm run run_testapp \\\" $moduleName\\\"\",",
            )

            if (currentLines.any { it.contains("test $moduleName") }) {
                return
            }

            currentLines.addAll(indexToAdd, newLines)

            files.write(path, File.create(it.getName(), FileContent(currentLines)))
        }
    }

    private fun updateTsConfigFiles(rootPath: Path, info: TypeScriptConfig, generateResult: GenerateResult, profile: HlaProfile) {
        val typeScriptPaths = TypeScriptPaths(
            mainTsconfig = rootPath.add(info.getMainTsconfigPath()),
            testTsconfig = rootPath.add(info.getTestTsconfigPath())
        )

        val moduleName = generateResult.getMain().getName().value
        updateTsConfigFile(typeScriptPaths.mainTsconfig, generateResult.getMain(), "${calculateFilePrefix(info.getMainTsconfigPath(), profile.getPaths().getSrc().getMain())}${moduleName}/")

        var fixturesAndTestDir = generateResult.getFixtures().copy()
        if (generateResult.getTests() != null) {
            fixturesAndTestDir = Directory.create(
                name = generateResult.getFixtures().getName(),
                files = emptyList(),
                directories = generateResult.getFixtures().getDirectories() + generateResult.getTests()!!.getDirectories()
            )
        }
        updateTsConfigFile(typeScriptPaths.testTsconfig, fixturesAndTestDir, "${calculateFilePrefix(info.getTestTsconfigPath(), profile.getPaths().getSrc().getTest())}${moduleName}/")
    }

    private fun calculateFilePrefix(tsconfigPath: Path, codePath: Path): String {
        val result = codePath.subtract(tsconfigPath).value
        return if (result.isEmpty()) {
            ""
        } else {
            "$result/"
        }
    }

    private fun updateTsConfigFile(tsconfigPath: Path, directory: Directory, prefix: String) {
        files.read(tsconfigPath.add(FileName("tsconfig.json"))).let {
            val currentLines = it.getContent().lines.toMutableList()

            val startIndex = currentLines.indexOfFirst { it.contains("\"files\"") || it.contains("\"include\"") }
            var indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfFirst { it.contains("]") } + startIndex
            val padding = currentLines[indexToAdd].takeWhile { it == ' ' } + "    "

            val newLines = mutableListOf<String>()
            extractFiles(directory).forEach { item ->
                newLines.add("")
                item.fileNames.forEach { fileName ->
                    newLines.add("$padding\"$prefix${item.submoduleName}/$fileName\",")
                    val result = currentLines.removeIf { line -> line.contains("$prefix${item.submoduleName}/$fileName") }
                    if (result) {
                        indexToAdd--
                    }
                }
            }

            currentLines.addAll(indexToAdd, newLines)
            val indexesToRemove = mutableListOf<Int>()
            currentLines.forEachIndexed { index, line ->
                if (line.isBlank() && currentLines.getOrNull(index - 1)?.isBlank() == true) {
                    indexesToRemove.add(index)
                }
            }
            indexesToRemove.reversed().forEach { currentLines.removeAt(it) }

            val newFile = File.create(it.getName(), FileContent(currentLines))
            if (newFile == it) {
                return
            }

            files.write(tsconfigPath, File.create(it.getName(), FileContent(currentLines)))
        }
    }

    data class TypeScriptPaths(val mainTsconfig: Path, val testTsconfig: Path)
    data class ExtractedFile(val submoduleName: String, val fileNames: List<String>)

    private fun extractFiles(dir: Directory): List<ExtractedFile> {
        return dir.getDirectories().map { subDir ->
            ExtractedFile(subDir.getName().value, subDir.getFiles().map { it.getName().value })
        }
    }
}