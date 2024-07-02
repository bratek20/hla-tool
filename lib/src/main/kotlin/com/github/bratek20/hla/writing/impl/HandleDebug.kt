package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.directory.impl.DirectoriesLogic
import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.GenerateResult

fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
    return profile.getLanguage() == ModuleLanguage.KOTLIN && moduleName.equals("OtherModule", ignoreCase = true)
}

fun handleDebug(generateResult: GenerateResult) {
    val debugPath = Path("../tmp")
    val dirs = DirectoriesLogic()
    dirs.delete(debugPath)
    dirs.write(debugPath, generateResult.getMain())
    dirs.write(debugPath, generateResult.getFixtures())
    generateResult.getTests()?.let { dirs.write(debugPath, it) }
}