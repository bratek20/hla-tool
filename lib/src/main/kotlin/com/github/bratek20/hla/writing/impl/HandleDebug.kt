package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.GeneratedModule
import com.github.bratek20.hla.writing.api.WriteArgs
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.impl.DirectoriesLogic

fun shouldHandleDebug(args: WriteArgs): Boolean {
    val module = args.getModule()
    val profile = args.getProfile()

    val moduleName = module.getName().value
    return profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && moduleName.equals("OtherModule", ignoreCase = true)
}

fun handleDebug(module: GeneratedModule) {
    val debugPath = Path("../tmp")
    val dirs = DirectoriesLogic()
    dirs.delete(debugPath)

    val finalDirs = mutableListOf<Directory>()

    module.getSubmodules().forEach { subModule ->
        val patterns = subModule.getPatterns()
        patterns.forEach {
            if(it.getFile() != null) {
                finalDirs.add(
                    Directory.create(
                        name = DirectoryName(it.getName().name),
                        files = listOf(it.getFile()!!)
                    )
                )
            } else if (it.getDirectory() != null) {
                finalDirs.add(it.getDirectory()!!)
            }
        }

    }
    val moduleDir = Directory.create(
        name = DirectoryName(module.getName().value),
        directories = finalDirs
    )
    dirs.write(debugPath, moduleDir)
}