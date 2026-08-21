package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.writing.impl.calcModuleDirectoryName
import com.github.bratek20.hla.writing.impl.calcSubmoduleDirectoryName
import com.github.bratek20.hla.writing.impl.getPathForSubmodule
import com.github.bratek20.hla.writing.impl.pathParts
import com.github.bratek20.hla.writing.impl.relativeModuleSpecifier

data class ModernTypeScriptFile(
    val module: ModuleName,
    val submodule: SubmoduleName,
    val pattern: PatternName,
)

fun HlaTypePath.asModernTypeScriptFile(): ModernTypeScriptFile {
    return ModernTypeScriptFile(getModuleName(), getSubmoduleName(), getPatternName())
}

// PatternName is the file name for every TypeScript pattern except Events, which
// EventsGenerator writes as Notifications.ts
private fun patternFileName(pattern: PatternName): String {
    return if (pattern == PatternName.Events) "Notifications" else pattern.name
}

class ModernTypeScriptPaths(private val profile: HlaProfile) {
    fun calculateImportPath(from: ModernTypeScriptFile, to: ModernTypeScriptFile): String {
        return relativeModuleSpecifier(
            directoryParts(from),
            directoryParts(to) + patternFileName(to.pattern)
        )
    }

    private fun directoryParts(file: ModernTypeScriptFile): List<String> {
        val srcRoot = profile.getPaths().getSrc().getPathForSubmodule(file.submodule)
        return pathParts(srcRoot.value) +
            calcModuleDirectoryName(file.module, profile).value +
            calcSubmoduleDirectoryName(file.submodule, profile).value
    }
}
