package pl.bratek20.hla.writing.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.facade.api.HlaProfile
import pl.bratek20.hla.generation.api.GenerateResult

data class WriteArgs(
    val hlaFolderPath: Path,
    val generateResult: GenerateResult,
    val profile: HlaProfile
)

interface ModuleWriter {
    fun write(args: WriteArgs)
}