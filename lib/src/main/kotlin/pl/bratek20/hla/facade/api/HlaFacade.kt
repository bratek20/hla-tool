package pl.bratek20.hla.facade.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName

data class GenerateModuleArgs(
    val moduleName: ModuleName,
    val language: ModuleLanguage,
    val inPath: Path,
    val outPath: Path
)

interface HlaFacade {
    fun generateModule(args: GenerateModuleArgs)
}