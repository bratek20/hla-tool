package pl.bratek20.hla.generation.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.definitions.ModuleDefinition

data class ModuleName(val value: String)

enum class ModuleLanguage {
    KOTLIN,
    TYPE_SCRIPT,
}

interface ModuleGenerator {
    fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<ModuleDefinition>): Directory
}