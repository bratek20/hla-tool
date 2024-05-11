package pl.bratek20.hla.writing.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.generation.api.GenerateResult

interface ModuleWriter {
    fun write(projectPath: Path, generateResult: GenerateResult)
}