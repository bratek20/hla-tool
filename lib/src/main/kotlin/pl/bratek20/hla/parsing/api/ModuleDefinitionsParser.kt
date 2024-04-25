package pl.bratek20.hla.parsing.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.definitions.ModuleDefinition

interface ModuleDefinitionsParser {
    fun parse(path: Path): List<ModuleDefinition>
}