package pl.bratek20.hla.facade.fixtures

import pl.bratek20.hla.facade.api.*

data class GenerateModuleArgsDef(
    var moduleName: String = "someValue",
    var language: ModuleLanguage = ModuleLanguage.KOTLIN,
    var hlaFolderPath: String = "someValue",
    var projectPath: String = "someValue",
)
fun generateModuleArgs(init: GenerateModuleArgsDef.() -> Unit = {}): GenerateModuleArgs {
    val def = GenerateModuleArgsDef().apply(init)
    return GenerateModuleArgs(
        moduleName = ModuleName(def.moduleName),
        language = def.language,
        hlaFolderPath = Path(def.hlaFolderPath),
        projectPath = Path(def.projectPath),
    )
}