package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.generation.impl.core.domain.ViewTypeFactory
import pl.bratek20.hla.model.*
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

abstract class ApiGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val languageTypes: LanguageTypes,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(languageTypes)
): DirectoryGenerator {
    abstract fun dirName(): String

    abstract fun valueObjectsFileName(): String
    abstract fun valueObjectsContentBuilder(): VelocityFileContentBuilder

    abstract fun interfacesFileName(): String
    abstract fun interfacesContentBuilder(): VelocityFileContentBuilder

    override fun generateDirectory(): Directory {
        val valueObjectsFile = valueObjectsFile()
        val interfacesFile = interfacesFile()

        return Directory(
            name = dirName(),
            files = listOf(
                valueObjectsFile,
                interfacesFile
            )
        )
    }

    private fun valueObjectsFile(): File {
        val fileContent = valueObjectsContentBuilder()
            .put("valueObjects", ValueObjectsView(
                simpleList = module.simpleValueObjects.map { toView(it) },
                complexList = module.complexValueObjects.map { toView(it) }
            ))
            .build()

        return File(
            name = valueObjectsFileName(),
            content = fileContent
        )
    }

    private fun interfacesFile(): File {
        val fileContent = interfacesContentBuilder()
            .put("interfaces", module.interfaces.map { toView(it) })
            .build()

        return File(
            name = interfacesFileName(),
            content = fileContent
        )
    }


    private fun toView(vo: SimpleValueObject): SimpleValueObjectView {
        return SimpleValueObjectView(
            name = vo.name,
            type = toViewType(vo.type())
        )
    }
    private fun toView(vo: ComplexValueObject): ComplexValueObjectView {
        return ComplexValueObjectView(
            name = vo.name,
            fields = vo.fields.map { FieldView(it.name, toViewType(it.type)) }
        )
    }

    private fun toView(interf: Interface): InterfaceView {
        return InterfaceView(
            name = interf.name,
            methods = interf.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = toViewType(method.returnType),
                    args = method.args.map { ArgumentView(it.name, toViewType(it.type)) }
                )
            }
        )
    }

    private fun toViewType(type: Type?): String {
        return viewTypeFactory.create(type, module).name()
    }
}