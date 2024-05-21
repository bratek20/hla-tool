package pl.bratek20.hla.facade.fixtures

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class HlaProfileDef(
    var name: String = "someValue",
    var language: ModuleLanguage = ModuleLanguage.KOTLIN,
    var projectPath: String = "someValue",
    var srcPath: String = "someValue",
    var fixturesPath: String = "someValue",
    var onlyParts: List<String> = emptyList(),
)
fun hlaProfile(init: HlaProfileDef.() -> Unit = {}): HlaProfile {
    val def = HlaProfileDef().apply(init)
    return HlaProfile(
        name = def.name,
        language = def.language,
        projectPath = def.projectPath,
        srcPath = def.srcPath,
        fixturesPath = def.fixturesPath,
        onlyParts = def.onlyParts,
    )
}

data class HlaPropertiesDef(
    var profiles: List<(HlaProfileDef.() -> Unit)> = emptyList(),
)
fun hlaProperties(init: HlaPropertiesDef.() -> Unit = {}): HlaProperties {
    val def = HlaPropertiesDef().apply(init)
    return HlaProperties(
        profiles = def.profiles.map { it -> hlaProfile(it) },
    )
}

data class ModuleOperationArgsDef(
    var hlaFolderPath: String = "someValue",
    var moduleName: String = "someValue",
    var profileName: String = "someValue",
)
fun moduleOperationArgs(init: ModuleOperationArgsDef.() -> Unit = {}): ModuleOperationArgs {
    val def = ModuleOperationArgsDef().apply(init)
    return ModuleOperationArgs(
        hlaFolderPath = pathCreate(def.hlaFolderPath),
        moduleName = ModuleName(def.moduleName),
        profileName = ProfileName(def.profileName),
    )
}