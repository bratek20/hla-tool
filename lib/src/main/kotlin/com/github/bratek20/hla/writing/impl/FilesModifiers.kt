package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.GeneratedModule
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.*

private const val TS_EXTENSION = ".ts"

// Fixtures and Tests are registered in the test tsconfig, every other submodule in the main one.
private val TEST_SUBMODULES = setOf(SubmoduleName.Fixtures, SubmoduleName.Tests)

private fun getSubmodulePath(profile: HlaProfile, submodule: SubmoduleName): Path {
    return profile.getPaths().getSrc().getPathForSubmodule(submodule)
}

class FilesModifiers(
    private val files: Files,
) {
    private data class SubmoduleFiles(
        val submodule: SubmoduleName,
        val fileNames: List<String>,
    )

    fun modify(args: WriteArgs, rootPath: Path) {
        val profile = args.getProfile()
        val info = profile.getTypeScript()

        if (profile.getLanguage() != ModuleLanguage.TYPE_SCRIPT || info == null || args.getOnlyUpdate()) {
            return
        }

        val module = args.getModule()
        val moduleName = module.getName().value
        val moduleDirectory = calcModuleDirectoryName(module.getName(), profile).value
        val (testFiles, mainFiles) = typeScriptFiles(module).partition { it.submodule in TEST_SUBMODULES }

        // Each of these files is maintained only when the profile says where it lives.
        // Whether a module gets the vitest flavour or the legacy test app flavour is the
        // module's own decision, so a modern profile can hold both kinds side by side.
        val modern = args.getModern()
        val vitestConfigPath = info.getVitestConfigPath()?.value ?: DEFAULT_VITEST_CONFIG_PATH

        info.getMainTsconfigPath()?.let { updateTsConfig(rootPath, it, moduleDirectory, mainFiles, profile) }
        info.getTestTsconfigPath()?.let { updateTsConfig(rootPath, it, moduleDirectory, testFiles, profile) }
        info.getPackageJsonPath()?.let {
            editFile(rootPath.add(it), PACKAGE_JSON) { lines ->
                if (modern) addModernTestScript(lines, moduleName, vitestConfigPath)
                else addLegacyTestScript(lines, moduleName)
            }
        }
        info.getLaunchJsonPath()?.let {
            editFile(rootPath.add(it), LAUNCH_JSON) { lines ->
                if (modern) addModernLaunchConfig(lines, moduleName, vitestConfigPath)
                else addLegacyLaunchConfig(lines, moduleName)
            }
        }
        if (modern) {
            info.getEntryPath()?.let { updateEntryFile(rootPath, it, module, profile) }
        }
    }

    private fun editFile(directory: Path, fileName: FileName, edit: (List<String>) -> List<String>) {
        val file = files.read(directory.add(fileName))
        val newLines = edit(file.getContent().lines)
        if (newLines != file.getContent().lines) {
            files.write(directory, File.create(file.getName(), FileContent(newLines)))
        }
    }

    private fun updateTsConfig(
        rootPath: Path,
        configPath: Path,
        moduleDirectory: String,
        submodulesFiles: List<SubmoduleFiles>,
        profile: HlaProfile
    ) {
        if (submodulesFiles.isEmpty()) {
            return
        }

        val configDirectory = getDirectoryPart(configPath)
        val submodulesPaths = submodulesFiles.map { filePaths(configDirectory, moduleDirectory, it, profile) }

        editFile(rootPath.add(configDirectory), getFileNamePart(configPath)) {
            addModuleFilesToTsConfig(it, moduleDirectory, submodulesPaths)
        }
    }

    // Paths as the tsconfig sees them: from the config directory down to the generated file.
    private fun filePaths(
        configDirectory: Path,
        moduleDirectory: String,
        submoduleFiles: SubmoduleFiles,
        profile: HlaProfile
    ): List<String> {
        val prefix = calculateFilePrefix(configDirectory, getSubmodulePath(profile, submoduleFiles.submodule))
        val submoduleDirectory = calcSubmoduleDirectoryName(submoduleFiles.submodule, profile).value

        return submoduleFiles.fileNames.map { "$prefix$moduleDirectory/$submoduleDirectory/$it" }
    }

    // Submodules that generated no TypeScript file at all are skipped - patterns writing a whole
    // directory (Examples) or a non TypeScript file (InitSql) are nothing to register.
    private fun typeScriptFiles(module: GeneratedModule): List<SubmoduleFiles> {
        return module.getSubmodules().mapNotNull { submodule ->
            val fileNames = submodule.getPatterns()
                .mapNotNull { it.getFile()?.getName()?.value }
                .filter { it.endsWith(TS_EXTENSION) }

            if (fileNames.isEmpty()) null else SubmoduleFiles(submodule.getName(), fileNames)
        }
    }

    private fun patternFileName(module: GeneratedModule, submodule: SubmoduleName, pattern: PatternName): String? {
        return module.getSubmodules().find { it.getName() == submodule }
            ?.getPatterns()?.find { it.getName() == pattern }
            ?.getFile()?.getName()?.value
    }

    // One side effect import per module, pointing at the file that pulls in the module's
    // registrations. The entry file must already exist, like every other file spliced here.
    private fun updateEntryFile(
        rootPath: Path,
        entryPath: Path,
        module: GeneratedModule,
        profile: HlaProfile
    ) {
        val target = findEntryTarget(module) ?: return

        val moduleDirectory = calcModuleDirectoryName(module.getName(), profile).value
        val importLine = "import \"${entrySpecifier(entryPath, moduleDirectory, target, profile)}\""

        editFile(rootPath.add(getDirectoryPart(entryPath)), getFileNamePart(entryPath)) {
            addEntryImport(it, importLine)
        }
    }

    private data class EntryTarget(val submodule: SubmoduleName, val fileBaseName: String)

    private fun findEntryTarget(module: GeneratedModule): EntryTarget? {
        return ENTRY_CANDIDATES.firstNotNullOfOrNull { (submodule, pattern) ->
            patternFileName(module, submodule, pattern)?.let {
                EntryTarget(submodule, it.removeSuffix(TS_EXTENSION))
            }
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

    private fun addLegacyLaunchConfig(lines: List<String>, moduleName: String): List<String> {
        if (lines.any { it.contains("Launch Test App - $moduleName Tests") }) {
            return lines
        }

        val result = lines.toMutableList()
        val startIndex = result.indexOfFirst { it.contains("\"configurations\"") }
        val paddingIndex = result.subList(startIndex, result.size)
            .indexOfLast { it.contains("workspaceFolder") } + startIndex + 2
        val padding = indentationOf(result[paddingIndex])

        result.addAll(paddingIndex + 1, listOf(
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
        ))

        return result
    }

    private fun addLegacyTestScript(lines: List<String>, moduleName: String): List<String> {
        if (lines.any { it.contains("test $moduleName") }) {
            return lines
        }

        val result = lines.toMutableList()
        val startIndex = result.indexOfFirst { it.contains("\"scripts\"") }
        val indexToAdd = result.subList(startIndex, result.size)
            .indexOfLast { it.contains("test ") } + startIndex + 1
        val padding = indentationOf(result[indexToAdd - 1])

        result[indexToAdd - 1] = result[indexToAdd - 1] + ","
        result.add(
            indexToAdd,
            "$padding\"test $moduleName\": \"npm run build_testapp && npm run run_testapp \\\" $moduleName\\\"\""
        )

        return result
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

    companion object {
        // Importing the web file pulls in ImplContext and then Logic, so the module's
        // registrations run. Modules with neither get no entry line - nothing to register.
        private val ENTRY_CANDIDATES = listOf(
            SubmoduleName.Web to PatternName.PlayFabHandlers,
            SubmoduleName.Impl to PatternName.ImplContext,
        )

        private const val DEFAULT_VITEST_CONFIG_PATH = "./vitest.config.mts"
        private val PACKAGE_JSON = FileName("package.json")
        private val LAUNCH_JSON = FileName("launch.json")
    }
}
