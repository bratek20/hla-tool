package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.file.File
import pl.bratek20.hla.generation.api.CodeGenerator
import pl.bratek20.hla.model.HlaModule

class CodeGeneratorLogic : CodeGenerator {
    override fun generateCode(module: HlaModule): File {
        return File(module.name, "")
    }
}