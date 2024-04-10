package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.Interface
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

abstract class ApiGenerator(
    private val module: HlaModule,
    private val velocity: VelocityFacade
) {
    abstract fun dirName(): String

    fun generateCode(): Directory {
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
            name = dirName(),
            files = simpleValueObjectFiles + complexValueObjectFiles + interfaceFiles,
        )
    }

    private fun simpleValueObjectFile(moduleName: String, vo: SimpleValueObject): File {
        val fileContent = contentBuilder("templates/kotlin/simpleValueObject.vm", moduleName)
            .put("vo", vo)
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    private fun complexValueObjectFile(moduleName: String, vo: ComplexValueObject): File {
        val fileContent = contentBuilder("templates/kotlin/complexValueObject.vm", moduleName)
            .put("vo", vo)
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    private fun interfaceFile(moduleName: String, interf: Interface): File {
        val fileContent = contentBuilder("templates/kotlin/interface.vm", moduleName)
            .put("interface", interf)
            .build()

        return File(
            name = interf.name + ".kt",
            content = fileContent
        )
    }

    private fun contentBuilder(templatePath: String, moduleName: String): VelocityFileContentBuilder {
        return contentBuilder(velocity, templatePath, moduleName)
    }
}