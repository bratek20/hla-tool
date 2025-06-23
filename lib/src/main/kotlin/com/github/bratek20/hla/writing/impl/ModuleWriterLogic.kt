package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.HlaSrcPaths
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.*

fun calcModuleDirectoryName(name: ModuleName, profile: HlaProfile): DirectoryName {
    if (profile.getLanguage() == ModuleLanguage.KOTLIN) {
        return DirectoryName(name.value.lowercase())
    }
    return DirectoryName(name.value)
}

fun calcSubmoduleDirectoryName(name: SubmoduleName, profile: HlaProfile): DirectoryName {
    if (profile.getLanguage() == ModuleLanguage.KOTLIN) {
        return DirectoryName(name.name.lowercase())
    }
    return DirectoryName(name.name)
}


fun HlaSrcPaths.getPathForSubmodule(submodule: SubmoduleName): Path {
    return getOverrides().firstOrNull {
        it.getSubmodule() == submodule || it.getSubmodules().contains(submodule)
    }?.getPath() ?: getDefault()
}

class ModuleWriterLogic(
    private val directories: Directories,
    private val filesModifiers: FilesModifiers
): ModuleWriter {
    override fun write(args: WriteArgs) {
        val rootPath = args.getHlaFolderPath().add(args.getProfile().getPaths().getProject())

        writeDirectories(args, rootPath)

        filesModifiers.modify(args, rootPath)

        if (shouldHandleDebug(args)) {
            handleDebug(args.getModule())
        }
    }

    private fun writeDirectories(
        args: WriteArgs,
        rootPath: Path
    ) {
        val profile = args.getProfile()
        val module = args.getModule()

        val paths = args.getProfile().getPaths().getSrc()
        val toWriteModules: MutableMap<Path, Directory> = mutableMapOf()

        val examplePath = args.getProfile().getPaths().getExamples()
        val toWriteExamples: MutableMap<Path, Directory> = mutableMapOf()
        val examplesSubModulePatterns = module.getSubmodules().find{subModule -> subModule.getName() == SubmoduleName.Examples }?.getPatterns()
        if(examplesSubModulePatterns != null && examplePath != null) {
            val exampleFinalPath = args.getHlaFolderPath().add(examplePath)

            val examplesDir = toWriteExamples.computeIfAbsent(exampleFinalPath) {
                Directory.create(name = DirectoryName("/PlayFab"))
            }

            val dire = mutableListOf<Directory>()

            examplesSubModulePatterns.forEach { pattern ->
                val fileName = pattern.getFile().getName().value
                val dirName = fileName.split("/").firstOrNull() ?: ""
                val suffix = fileName.split("/").lastOrNull() ?: fileName
                val file = File(
                    name = suffix,
                    content = pattern.getFile().getContent().toString(),
                )
                dire.add(
                    Directory.create(
                        name = DirectoryName("${module.getName().value}/$dirName"),
                        files = listOf(file)
                    )
                )
            }

            val finalDir = examplesDir.copy(
                directories = examplesDir.getDirectories() + dire
            )
            toWriteExamples[exampleFinalPath] = finalDir
        }
        module.getSubmodules().forEach { sub ->
            val path = paths.getPathForSubmodule(sub.getName())

            val currentDir = toWriteModules.computeIfAbsent(path) {
                Directory.create(name = calcModuleDirectoryName(module.getName(), profile))
            }

            val patternsToMap = sub.getPatterns().filter { it.getName().name != SubmoduleName.Examples.name }

            val updatedDir = currentDir.copy(
                directories = currentDir.getDirectories() + Directory.create(
                    name = calcSubmoduleDirectoryName(sub.getName(), profile),
                    files = patternsToMap.map { it.getFile() }
                )
            )
            toWriteModules[path] = updatedDir
        }

        toWriteModules.forEach { (path, dir) -> directories.write(rootPath.add(path), dir) }
        toWriteExamples.forEach { (path, dir) -> directories.write(path, dir) }
    }
}