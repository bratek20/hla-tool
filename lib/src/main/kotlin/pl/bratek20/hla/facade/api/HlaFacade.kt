package pl.bratek20.hla.facade.api

import pl.bratek20.hla.definitions.api.ModuleName
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.generation.api.ModuleLanguage

data class GenerateModuleArgs(
    val moduleName: ModuleName,
    val language: ModuleLanguage,
    val hlaFolderPath: Path,
    val projectPath: Path
)

interface HlaFacade {
    fun generateModule(args: GenerateModuleArgs)
}