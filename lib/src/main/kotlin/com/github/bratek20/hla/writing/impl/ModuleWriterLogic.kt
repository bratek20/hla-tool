package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
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

class ModuleWriterLogic(
    private val directories: Directories,
    private val filesModifiers: FilesModifiers
): ModuleWriter {

    //TODO-REF: profile should be not needed, write should be aware what language is used
    //TODO-REF: calculation should be language strategy
    private fun calcModuleDirectoryName(name: ModuleName, profile: HlaProfile): DirectoryName {
        if (profile.getLanguage() == ModuleLanguage.KOTLIN) {
            return DirectoryName(name.value.lowercase())
        }
        return DirectoryName(name.value)
    }
    private fun calcSubmoduleDirectoryName(name: SubmoduleName, profile: HlaProfile): DirectoryName {
        if (profile.getLanguage() == ModuleLanguage.KOTLIN) {
            return DirectoryName(name.name.lowercase())
        }
        return DirectoryName(name.name)
    }

    //TODO-REF generic logic where each submodule should be written
    private fun calcGenerateResult(module: GeneratedModule, profile: HlaProfile): GenerateResult {
        val main = Directory.create(
            calcModuleDirectoryName(module.getName(), profile),
            directories = listOfNotNull(
                submoduleToDirectory(SubmoduleName.Api, module.getSubmodules(), profile),
                submoduleToDirectory(SubmoduleName.Impl, module.getSubmodules(), profile),
                submoduleToDirectory(SubmoduleName.Web, module.getSubmodules(), profile),
                submoduleToDirectory(SubmoduleName.ViewModel, module.getSubmodules(), profile),
                submoduleToDirectory(SubmoduleName.View, module.getSubmodules(), profile),
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
            files = sub.getPatterns().map { it.getFile() }
        )
    }
    override fun write(args: WriteArgs) {
        val rootPath = args.getHlaFolderPath().add(args.getProfile().getPaths().getProject())
        val generateResult = calcGenerateResult(args.getModule(), args.getProfile())
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
        generateResult.getFixtures()?.let { directories.write(paths.fixtures, it) }
        generateResult.getTests()?.let { directories.write(paths.test, it) }
    }

    private data class Paths(val main: Path, val fixtures: Path, val test: Path)
}