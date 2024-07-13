package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.generation.api.GenerateResult
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Path

class ModuleWriterLogic(
    private val directories: Directories,
    private val filesModifiers: FilesModifiers
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