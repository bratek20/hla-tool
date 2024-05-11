package pl.bratek20.hla.facade.api

import pl.bratek20.hla.directory.api.*

data class ModuleName(
    val value: String
)

data class GenerateModuleArgs(
    val moduleName: ModuleName,
    val language: ModuleLanguage,
    val hlaFolderPath: Path,
    val projectPath: Path,
)