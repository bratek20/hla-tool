package com.github.bratek20.hla.writing.impl

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.impl.DirectoriesLogic

fun shouldHandleDebug(profile: HlaProfile, moduleName: String): Boolean {
    return profile.getLanguage() == ModuleLanguage.TYPE_SCRIPT && moduleName.equals("OtherModule", ignoreCase = true)
}

fun handleDebug(generateResult: GenerateResult) {
    val debugPath = Path("../tmp")
    val dirs = DirectoriesLogic()
    dirs.delete(debugPath)
    dirs.write(debugPath, generateResult.getMain())
    dirs.write(debugPath, generateResult.getFixtures())
    generateResult.getTests()?.let { dirs.write(debugPath, it) }
}