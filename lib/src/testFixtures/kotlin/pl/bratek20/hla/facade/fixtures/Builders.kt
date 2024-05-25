package pl.bratek20.hla.facade.fixtures

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class HlaProfileDef(
    var name: String = "someValue",
    var language: ModuleLanguage = ModuleLanguage.KOTLIN,
    var projectPath: String = "someValue",
    var mainPath: String = "someValue",
    var fixturesPath: String = "someValue",
    var onlyParts: List<String> = emptyList(),
    var generateWeb: Boolean = false,
)
fun hlaProfile(init: HlaProfileDef.() -> Unit = {}): HlaProfile {
    val def = HlaProfileDef().apply(init)
    return HlaProfile(
        name = def.name,
        language = def.language,
        projectPath = def.projectPath,
        mainPath = def.mainPath,
        fixturesPath = def.fixturesPath,
        onlyParts = def.onlyParts,
        generateWeb = def.generateWeb,
    )
}

data class ModuleOperationArgsDef(
    var hlaFolderPath: String = "someValue",
    var profileName: String = "someValue",
    var moduleName: String = "someValue",
)
fun moduleOperationArgs(init: ModuleOperationArgsDef.() -> Unit = {}): ModuleOperationArgs {
    val def = ModuleOperationArgsDef().apply(init)
    return ModuleOperationArgs(
        hlaFolderPath = pathCreate(def.hlaFolderPath),
        profileName = ProfileName(def.profileName),
        moduleName = ModuleName(def.moduleName),
    )
}