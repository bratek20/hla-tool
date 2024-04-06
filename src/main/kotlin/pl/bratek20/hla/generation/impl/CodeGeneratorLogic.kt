package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContentBuilder
import pl.bratek20.hla.generation.api.CodeGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.SimpleValueObject

class CodeGeneratorLogic : CodeGenerator {
    override fun generateCode(module: HlaModule): Directory {
        return Directory(
            name = module.name.lowercase(),
            files = module.simpleValueObjects.map {
                simpleValueObjectFile(module.name, it)
            },
            directories = emptyList()
        )
    }

    private fun simpleValueObjectFile(moduleName: String, vo: SimpleValueObject): File {
        return File(
            name = vo.name + ".kt",
            content = FileContentBuilder()
                .addLine("package pl.bratek20.${moduleName.lowercase()}")
                .addLine("")
                .addLine("data class ${vo.name}(")
                .addLine("    val value: ${vo.type}")
                .addLine(")")
                .build()
        )
    }
}