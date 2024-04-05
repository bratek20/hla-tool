package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.directory.Directory
import pl.bratek20.hla.generation.api.CodeGenerator
import pl.bratek20.hla.model.HlaModule

class CodeGeneratorLogic : CodeGenerator {
    override fun generateCode(module: HlaModule): Directory {
        return Directory(
            name = module.name,
            files = emptyList(),
            directories = emptyList()
        )
    }
}