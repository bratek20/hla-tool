package pl.bratek20.hla.generation.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.facade.api.HlaProfile
import pl.bratek20.hla.facade.api.ModuleName

data class GenerateArgs(
    val moduleName: ModuleName,
    val modules: List<ModuleDefinition>,
    val onlyUpdate: Boolean,
    val profile: HlaProfile
)

data class GenerateResult(
    val main: Directory,
    val fixtures: Directory
)

interface ModuleGenerator {
    fun generate(args: GenerateArgs): GenerateResult
}