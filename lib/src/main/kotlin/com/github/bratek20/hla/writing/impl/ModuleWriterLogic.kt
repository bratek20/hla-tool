package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.generation.api.GeneratedModule
import com.github.bratek20.hla.generation.api.GeneratedSubmodule
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName
import com.github.bratek20.utils.directory.api.Path

class GenerateResult(
    private val main: Directory,
    private val fixtures: Directory,
    private val tests: Directory?,
) {
    fun getMain(): Directory {
        return main
    }

    fun getFixtures(): Directory {
        return fixtures
    }

    fun getTests(): Directory? {
        return tests
    }
}

class ModuleWriterLogic(
    private val directories: Directories,
    private val filesModifiers: FilesModifiers
): ModuleWriter {

    private fun calcGenerateResult(module: GeneratedModule): GenerateResult {
        val main = Directory.create(
            DirectoryName(module.getName().value),
            directories = listOfNotNull(
                submoduleToDirectory(SubmoduleName.Api, module.getSubmodules()),
                submoduleToDirectory(SubmoduleName.Impl, module.getSubmodules()),
                submoduleToDirectory(SubmoduleName.Web, module.getSubmodules()),
                submoduleToDirectory(SubmoduleName.Context, module.getSubmodules()),
            )
        );
        val fixtures = Directory.create(
            DirectoryName(module.getName().value),
            directories = listOfNotNull(
                submoduleToDirectory(SubmoduleName.Fixtures, module.getSubmodules()),
            )
        );
        val tests = Directory.create(
            DirectoryName(module.getName().value),
            directories = listOfNotNull(
                submoduleToDirectory(SubmoduleName.Tests, module.getSubmodules()),
            )
        );
        return GenerateResult(main, fixtures, tests)
    }

    private fun submoduleToDirectory(name: SubmoduleName, subs: List<GeneratedSubmodule>): Directory? {
        val sub = subs.find { it.getName() == name }
        if (sub == null || sub.getPatterns().isEmpty()) {
            return null
        }

        return Directory.create(
            name = DirectoryName(name.name),
            files = sub.getPatterns().map { it.getFile() }
        )
    }
    override fun write(args: WriteArgs) {
        val rootPath = args.getHlaFolderPath().add(args.getProfile().getPaths().getProject())
        val generateResult = calcGenerateResult(args.getModule())
        val profile = args.getProfile()

        val src = profile.getPaths().getSrc()
        val paths = Paths(
            main = rootPath.add(src.getMain()),
            fixtures = rootPath.add(src.getFixtures()),
            test = rootPath.add(src.getTest())
        )

        writeDirectories(paths, generateResult)

        filesModifiers.modify(profile, rootPath, generateResult, args.getOnlyUpdate())

        if (shouldHandleDebug(profile, generateResult.getMain().getName().value)) {
            handleDebug(generateResult)
        }
    }

    private fun writeDirectories(paths: Paths, generateResult: GenerateResult) {
        directories.write(paths.main, generateResult.getMain())
        directories.write(paths.fixtures, generateResult.getFixtures())
        generateResult.getTests()?.let { directories.write(paths.test, it) }
    }

    private data class Paths(val main: Path, val fixtures: Path, val test: Path)
}