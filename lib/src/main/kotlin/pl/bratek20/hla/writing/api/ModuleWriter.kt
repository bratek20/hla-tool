package pl.bratek20.hla.writing.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.facade.api.ModuleLanguage

data class WriteArgs(
    val projectPath: Path,
    val generateResult: GenerateResult,
    val language: ModuleLanguage
)

interface ModuleWriter {
    fun write(args: WriteArgs)
}