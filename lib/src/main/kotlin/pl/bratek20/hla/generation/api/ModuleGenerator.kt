package pl.bratek20.hla.generation.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.api.ModuleName

enum class ModuleLanguage {
    KOTLIN,
    TYPE_SCRIPT,
}

data class GenerateResult(
    val main: Directory,
    val testFixtures: Directory
)

interface ModuleGenerator {
    fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<ModuleDefinition>): GenerateResult
}