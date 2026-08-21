package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.GeneratedModule
import com.github.bratek20.hla.generation.api.GeneratedSubmodule
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.*

private fun getSubmodulePath(profile: HlaProfile, submodule: SubmoduleName): Path {
    return profile.getPaths().getSrc().getPathForSubmodule(submodule)
}

//TODO-REF GenerateResult is legacy structure, should be removed and use new concept of GeneratedModule
private fun calcGenerateResult(module: GeneratedModule, profile: HlaProfile): GenerateResult {
    val main = Directory.create(
        calcModuleDirectoryName(module.getName(), profile),
        directories = listOfNotNull(
            submoduleToDirectory(SubmoduleName.Api, module.getSubmodules(), profile),
            submoduleToDirectory(SubmoduleName.Impl, module.getSubmodules(), profile),
            submoduleToDirectory(SubmoduleName.Web, module.getSubmodules(), profile),
            submoduleToDirectory(SubmoduleName.Context, module.getSubmodules(), profile),
        )
    );
    val fixtures = Directory.create(
        calcModuleDirectoryName(module.getName(), profile),
        directories = listOfNotNull(
            submoduleToDirectory(SubmoduleName.Fixtures, module.getSubmodules(), profile),
        )
    );
    val tests = Directory.create(
        calcModuleDirectoryName(module.getName(), profile),
        directories = listOfNotNull(
            submoduleToDirectory(SubmoduleName.Tests, module.getSubmodules(), profile),
        )
    );
    return GenerateResult(
        main,
        toNullIfEmpty(fixtures),
        toNullIfEmpty(tests)
    )
}

private class GenerateResult(
    private val main: Directory,
    private val fixtures: Directory?,
    private val tests: Directory?,
) {
    fun getMain(): Directory {
        return main
    }

    fun getFixtures(): Directory? {
        return fixtures
    }

    fun getTests(): Directory? {
        return tests
    }
}

private fun toNullIfEmpty(directory: Directory): Directory? {
    return if (directory.getDirectories().isEmpty() && directory.getFiles().isEmpty()) {
        null
    } else {
        directory
    }
}

private fun submoduleToDirectory(name: SubmoduleName, subs: List<GeneratedSubmodule>, profile: HlaProfile): Directory? {
    val sub = subs.find { it.getName() == name }
    if (sub == null || sub.getPatterns().isEmpty()) {
        return null
    }

    return Directory.create(
        name = calcSubmoduleDirectoryName(name, profile),
        files = sub.getPatterns().map { it.getFile()!! }
    )
}

class FilesModifiers(
    private val files: Files,
) {
    fun modify(args: WriteArgs, rootPath: Path) {
        val profile = args.getProfile()
        val info = profile.getTypeScript()

        if (profile.getLanguage() != ModuleLanguage.TYPE_SCRIPT || info == null || args.getOnlyUpdate()) {
            return
        }

        val generateResult = calcGenerateResult(args.getModule(), profile)
        val moduleName = generateResult.getMain().getName().value

        // Each of these files is maintained only when the profile says where it lives.
        // Modern profiles skip the tsconfig paths, because imports make the ordered file
        // list pointless, and get the vitest flavour of the scripts and debug configs.
        val modern = info.getModern() == true
        val vitestConfigPath = info.getVitestConfigPath()?.value ?: DEFAULT_VITEST_CONFIG_PATH

        info.getMainTsconfigPath()?.let { updateMainTsConfig(rootPath, it, generateResult, profile) }
        info.getTestTsconfigPath()?.let { updateTestTsConfig(rootPath, it, generateResult, profile) }
        info.getPackageJsonPath()?.let {
            if (modern) updateModernPackageJson(rootPath, it, moduleName, vitestConfigPath)
            else updatePackageJson(rootPath, it, moduleName)
        }
        info.getLaunchJsonPath()?.let {
            if (modern) updateModernLaunchJson(rootPath, it, moduleName, vitestConfigPath)
            else updateLaunchJson(rootPath, it, moduleName)
        }
        info.getEntryPath()?.let { updateEntryFile(rootPath, it, generateResult, profile) }
    }

    // Modern modules run on vitest, so both the npm script and the debug configuration
    // look nothing like their legacy build_testapp counterparts.
    private fun updateModernPackageJson(
        rootPath: Path,
        packageJsonPath: Path,
        moduleName: String,
        vitestConfigPath: String
    ) {
        editJsonFile(rootPath.add(packageJsonPath), PACKAGE_JSON) {
            addModernTestScript(it, moduleName, vitestConfigPath)
        }
    }

    private fun updateModernLaunchJson(
        rootPath: Path,
        launchJsonPath: Path,
        moduleName: String,
        vitestConfigPath: String
    ) {
        editJsonFile(rootPath.add(launchJsonPath), LAUNCH_JSON) {
            addModernLaunchConfig(it, moduleName, vitestConfigPath)
        }
    }

    private fun editJsonFile(directory: Path, fileName: FileName, edit: (List<String>) -> List<String>) {
        val file = files.read(directory.add(fileName))
        val newLines = edit(file.getContent().lines)
        if (newLines != file.getContent().lines) {
            files.write(directory, File.create(file.getName(), FileContent(newLines)))
        }
    }

    // One side effect import per module, pointing at the file that pulls in the module's
    // registrations. The entry file must already exist, like every other file spliced here.
    private fun updateEntryFile(
        rootPath: Path,
        entryPath: Path,
        generateResult: GenerateResult,
        profile: HlaProfile
    ) {
        val target = findEntrySideEffectFile(generateResult.getMain(), profile) ?: return

        val directory = rootPath.add(getDirectoryPart(entryPath))
        val fileName = getFileNamePart(entryPath)
        val file = files.read(directory.add(fileName))

        val moduleDirectory = generateResult.getMain().getName().value
        val importLine = "import \"${entrySpecifier(entryPath, moduleDirectory, target, profile)}\""

        val currentLines = file.getContent().lines
        val newLines = addEntryImport(currentLines, importLine)
        if (newLines != currentLines) {
            files.write(directory, File.create(fileName, FileContent(newLines)))
        }
    }

    private data class EntryTarget(val submodule: SubmoduleName, val fileBaseName: String)

    private fun findEntrySideEffectFile(main: Directory, profile: HlaProfile): EntryTarget? {
        return ENTRY_CANDIDATES.firstOrNull { candidate ->
            main.getDirectories()
                .find { it.getName().value == calcSubmoduleDirectoryName(candidate.submodule, profile).value }
                ?.getFiles()
                ?.any { it.getName().value == candidate.fileBaseName + ".ts" } == true
        }
    }

    private fun entrySpecifier(
        entryPath: Path,
        moduleDirectory: String,
        target: EntryTarget,
        profile: HlaProfile
    ): String {
        val targetParts = pathParts(getSubmodulePath(profile, target.submodule).value) +
            moduleDirectory +
            calcSubmoduleDirectoryName(target.submodule, profile).value +
            target.fileBaseName

        return relativeModuleSpecifier(pathParts(getDirectoryPart(entryPath).value), targetParts)
    }

    private fun updateLaunchJson(rootPath: Path, launchJsonPath: Path, moduleName: String) {
        val path = rootPath.add(launchJsonPath)
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
                "$padding    \"program\": \"\${workspaceFolder}/Dist/AFC.testapp.js\",",
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

    private fun updatePackageJson(rootPath: Path, packageJsonPath: Path, moduleName: String) {
        val path = rootPath.add(packageJsonPath)
        files.read(path.add(FileName("package.json"))).let {
            val currentLines = it.getContent().lines.toMutableList()
            val startIndex = currentLines.indexOfFirst { it.contains("\"scripts\"") }
            val indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfLast { it.contains("test ") } + startIndex + 1
            val padding = currentLines[indexToAdd-1].takeWhile { it == ' ' }
            val newLines = listOf(
                "$padding\"test $moduleName\": \"npm run build_testapp && npm run run_testapp \\\" $moduleName\\\"\"",
            )

            if (currentLines.any { it.contains("test $moduleName") }) {
                return
            }

            currentLines[indexToAdd-1] = currentLines[indexToAdd-1] + ","
            currentLines.addAll(indexToAdd, newLines)

            files.write(path, File.create(it.getName(), FileContent(currentLines)))
        }
    }

    //TODO-REF a lot of duplication, similar methods etc
    private fun updateMainTsConfig(
        rootPath: Path,
        configPath: Path,
        generateResult: GenerateResult,
        profile: HlaProfile
    ) {
        val directoryPath = getDirectoryPart(configPath)
        val moduleName = generateResult.getMain().getName().value

        updateTsConfigFileAndWrite(
            rootPath.add(directoryPath),
            generateResult.getMain(),
            "${calculateFilePrefix(directoryPath, profile.getPaths().getSrc().getDefault())}${moduleName}/",
            getFileNamePart(configPath)
        )
    }

    private fun updateTestTsConfig(
        rootPath: Path,
        configPath: Path,
        generateResult: GenerateResult,
        profile: HlaProfile
    ) {
        val directoryPath = getDirectoryPart(configPath)
        val testTsconfig = rootPath.add(directoryPath)
        val moduleName = generateResult.getMain().getName().value

        val initialTestFile = files.read(testTsconfig.add(getFileNamePart(configPath)))
        var testFile: File = initialTestFile
        generateResult.getFixtures()?.let {
            val x = updateTsConfigFile(testFile, it, "${calculateFilePrefix(directoryPath, getSubmodulePath(profile, SubmoduleName.Fixtures))}${moduleName}/")
            testFile = x ?: testFile
        }
        generateResult.getTests()?.let {
            val x = updateTsConfigFile(testFile, it, "${calculateFilePrefix(directoryPath, getSubmodulePath(profile, SubmoduleName.Tests))}${moduleName}/")
            testFile = x ?: testFile
        }
        if (testFile != initialTestFile) {
            files.write(testTsconfig, testFile)
        }
    }

    private fun getDirectoryPart(path: Path): Path {
        val stringPath = path.toString()
        return if(stringPath.contains("/")) {
            Path(stringPath.substringBeforeLast("/"))
        }else {
            Path("")
        }
    }

    private fun getFileNamePart(path: Path): FileName {
        val stringPath = path.toString()
        return if(stringPath.contains("/")) {
            FileName(stringPath.substringAfterLast("/"))
        }else {
            FileName(stringPath)
        }
    }

    private fun calculateFilePrefix(tsconfigPath: Path, codePath: Path): String {
        if(!codePath.toString().contains(tsconfigPath.toString())) {
            if(tsconfigPath.toString().isNotEmpty()) {
                val upperFolderCount = tsconfigPath.toString().count { it == '/' } + 1
                return "../".repeat(upperFolderCount) + "${codePath}/"
            }
            return "${codePath}/"
        }
        val result = codePath.subtract(tsconfigPath).value
        return if (result.isEmpty()) {
            ""
        } else {
            "$result/"
        }
    }

    private fun updateTsConfigFileAndWrite(
        tsconfigPath: Path,
        directory: Directory,
        prefix: String,
        configFileName: FileName
    ) {
        val x = updateTsConfigFile(files.read(tsconfigPath.add(configFileName)), directory, prefix)
        x?.let { files.write(tsconfigPath, it) }
    }

    private fun updateTsConfigFile(file: File, directory: Directory, prefix: String): File? {
        val currentLines = file.getContent().lines.toMutableList()

        val startIndex = currentLines.indexOfFirst { it.contains("\"files\"") || it.contains("\"include\"") }
        var indexToAdd = currentLines.subList(startIndex, currentLines.size).indexOfFirst { it.contains("]")} + startIndex
        val padding = currentLines[indexToAdd].takeWhile { it == ' ' } + "    "

        val newLines = mutableListOf<String>()
        val directoryName = directory.getName().value
        val moduleStartComment = "$padding//$directoryName start"
        val moduleEndComment = "$padding//$directoryName end"
        extractFiles(directory).forEachIndexed { index, item ->
            newLines.add("")
            if(index == 0) {
               newLines.add(moduleStartComment)
            }
            item.fileNames
                .filter { it.endsWith(".ts") }
                .forEach { fileName ->
                    newLines.add("$padding\"$prefix${item.submoduleName}/$fileName\",")
                    val result = currentLines.removeIf { line -> line.contains("\"$prefix${item.submoduleName}/$fileName")}
                    if (result) {
                        indexToAdd--
                    }
                }
        }

        newLines.add(moduleEndComment)


        currentLines.addAll(indexToAdd, newLines)

        cleanStartEndComments(currentLines, moduleStartComment, moduleEndComment)
        cleanWhiteLines(currentLines, directoryName)

        val newFile = File.create(file.getName(), FileContent(currentLines))
        if (newFile == file) {
            return null
        }

        return File.create(file.getName(), FileContent(currentLines))
    }

    private fun cleanWhiteLines(currentLines: MutableList<String>, directoryName: String) {
        val indexesToRemove = mutableListOf<Int>()
        currentLines.forEachIndexed { index, line ->
            if (line.isBlank() && currentLines.getOrNull(index - 1)?.isBlank() == true ||
                line.isBlank() && currentLines.getOrNull(index - 1)?.contains("//$directoryName start") == true
            ) {
                indexesToRemove.add(index)
            }
        }
        indexesToRemove.reversed().forEach { currentLines.removeAt(it) }
    }

    private fun cleanStartEndComments(
        currentLines: MutableList<String>,
        moduleStartComment: String,
        moduleEndComment: String
    ) {
        val indexesToRemove = mutableListOf<Int>()
        val firstModuleStartCommentIndex = currentLines.indexOfFirst { it.contains(moduleStartComment) }
        val lastModuleEndCommentIndex = currentLines.indexOfLast { it.contains(moduleEndComment) }
        currentLines.forEachIndexed { index, line ->
            if (line == moduleStartComment && index != firstModuleStartCommentIndex || line == moduleEndComment && index != lastModuleEndCommentIndex) {
                indexesToRemove.add(index)
            }
        }
        indexesToRemove.reversed().forEach { currentLines.removeAt(it) }
    }

    data class ExtractedFile(val submoduleName: String, val fileNames: List<String>)

    private fun extractFiles(dir: Directory): List<ExtractedFile> {
        return dir.getDirectories().map { subDir ->
            ExtractedFile(subDir.getName().value, subDir.getFiles().map { it.getName().value })
        }
    }

    companion object {
        // Importing the web file pulls in ImplContext and then Logic, so the module's
        // registrations run. Modules with neither get no entry line - nothing to register.
        private val ENTRY_CANDIDATES = listOf(
            EntryTarget(SubmoduleName.Web, "PlayFabHandlers"),
            EntryTarget(SubmoduleName.Impl, "ImplContext"),
        )

        private const val DEFAULT_VITEST_CONFIG_PATH = "./vitest.config.mts"
        private val PACKAGE_JSON = FileName("package.json")
        private val LAUNCH_JSON = FileName("launch.json")
    }
}