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
    return profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && moduleName.equals("SomeModule", ignoreCase = true)
}

fun handleDebug(module: GeneratedModule) {
    val debugPath = Path("../tmp")
    val dirs = DirectoriesLogic()
    dirs.delete(debugPath)

    val moduleDir = Directory.create(
        name = DirectoryName(module.getName().value),
        directories = module.getSubmodules().map {
            Directory.create(
                name = DirectoryName(it.getName().name),
                files = it.getPatterns().map { p -> p.getFile() }
            )
        }
    )
    dirs.write(debugPath, moduleDir)
}