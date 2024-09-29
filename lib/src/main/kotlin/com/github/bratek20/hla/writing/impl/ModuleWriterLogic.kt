package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName
import com.github.bratek20.utils.directory.api.Path

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
        val toWrite: MutableMap<Path, Directory> = mutableMapOf()

        module.getSubmodules().forEach { sub ->
            val path = paths.getOverrides()
                .firstOrNull { it.getSubmodule() == sub.getName() }
                ?.getPath()
                ?: paths.getDefault()

            val currentDir = toWrite.computeIfAbsent(path) {
                Directory.create(name = calcModuleDirectoryName(module.getName(), profile))
            }

            val updatedDir = currentDir.copy(
                directories = currentDir.getDirectories() + Directory.create(
                    name = calcSubmoduleDirectoryName(sub.getName(), profile),
                    files = sub.getPatterns().map { it.getFile() }
                )
            )
            toWrite[path] = updatedDir
        }

        toWrite.forEach { (path, dir) -> directories.write(rootPath.add(path), dir) }
    }
}