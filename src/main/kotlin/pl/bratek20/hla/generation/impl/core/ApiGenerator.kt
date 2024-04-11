package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.*
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

abstract class ApiGenerator(
    private val module: HlaModule,
    private val velocity: VelocityFacade,
    private val types: Types
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
        val fileContent = contentBuilder("simpleValueObject.vm", moduleName)
            .put("vo", toView(vo))
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    data class FieldView(
        val name: String,
        val type: String
    )
    data class ComplexValueObjectView(
        val name: String,
        val fields: List<FieldView>
    )
    data class SimpleValueObjectView(
        val name: String,
        val type: String
    )
    private fun toView(vo: SimpleValueObject): SimpleValueObjectView {
        return SimpleValueObjectView(
            name = vo.name,
            type = types.map(vo.type)
        )
    }
    private fun toView(vo: ComplexValueObject): ComplexValueObjectView {
        return ComplexValueObjectView(
            name = vo.name,
            fields = vo.fields.map { FieldView(it.name, types.map(it.type)) }
        )
    }

    private fun complexValueObjectFile(moduleName: String, vo: ComplexValueObject): File {
        val fileContent = contentBuilder("complexValueObject.vm", moduleName)
            .put("vo", toView(vo))
            .build()

        return File(
            name = vo.name + ".kt",
            content = fileContent
        )
    }

    data class ArgumentView(
        val name: String,
        val type: String
    )
    data class MethodView(
        val name: String,
        val returnType: String?,
        val args: List<ArgumentView>
    )
    data class InterfaceView(
        val name: String,
        val methods: List<MethodView>
    )

    private fun toView(interf: Interface): InterfaceView {
        return InterfaceView(
            name = interf.name,
            methods = interf.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = method.returnType?.let { types.map(it) },
                    args = method.args.map { ArgumentView(it.name, types.map(it.type)) }
                )
            }
        )
    }
    private fun interfaceFile(moduleName: String, interf: Interface): File {
        val fileContent = contentBuilder("interface.vm", moduleName)
            .put("interface", toView(interf))
            .build()

        return File(
            name = interf.name + ".kt",
            content = fileContent
        )
    }

    private fun contentBuilder(templateName: String, moduleName: String): VelocityFileContentBuilder {
        return pl.bratek20.hla.generation.impl.languages.kotlin.kotlinContentBuilder(velocity, templateName, moduleName)
    }
}