package pl.bratek20.hla.generation.api

import pl.bratek20.hla.file.File
import pl.bratek20.hla.model.HlaModule

interface CodeGenerator {
    fun generateCode(module: HlaModule): File
}