package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.api.CodeGenerator
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.Interface
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class CodeGeneratorLogic : CodeGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generateCode(module: HlaModule): Directory {
        val simpleValueObjectFiles = module.simpleValueObjects.map {
            simpleValueObjectFile(module.name, it)
        }
        val complexValueObjectFiles = module.complexValueObjects.map {
            complexValueObjectFile(module.name, it)
        }
        val interfaceFiles = module.interfaces.map {
            interfaceFile(module.name, it)
        }

        return Directory(
            name = module.name.lowercase(),
            files = simpleValueObjectFiles + complexValueObjectFiles + interfaceFiles,
            directories = emptyList()
        )
    }

    private fun simpleValueObjectFile(moduleName: String, vo: SimpleValueObject): File {
        val fileContent = velocity.contentBuilder("templates/simpleValueObject.vm")
            .put("packageName", "pl.bratek20.${moduleName.lowercase()}")
            .put("vo", vo)
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    private fun complexValueObjectFile(moduleName: String, vo: ComplexValueObject): File {
        val fileContent = velocity.contentBuilder("templates/complexValueObject.vm")
            .put("packageName", "pl.bratek20.${moduleName.lowercase()}")
            .put("vo", vo)
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    private fun interfaceFile(moduleName: String, interf: Interface): File {
        val fileContent = velocity.contentBuilder("templates/interface.vm")
            .put("packageName", "pl.bratek20.${moduleName.lowercase()}")
            .put("interface", interf)
            .build()

        return File(
            name = interf.name + ".kt",
            content = fileContent
        )
    }
}