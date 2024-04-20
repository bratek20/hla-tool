package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModulePartDirectoryGenerator
import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.generation.impl.core.domain.ViewTypeFactory
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.Interface
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.model.Type
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

abstract class ApiGenerator(
    c: ModulePartGeneratorContext,
    private val languageTypes: LanguageTypes,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(languageTypes)
): ModulePartDirectoryGenerator(c) {
    abstract fun dirName(): String

    abstract fun valueObjectsFileName(): String
    abstract fun valueObjectsContentBuilder(): VelocityFileContentBuilder

    abstract fun interfacesFileName(): String
    abstract fun interfacesContentBuilder(): VelocityFileContentBuilder

    override fun generateDirectory(): Directory {
        val valueObjectsFile = valueObjectsFile()
        val interfacesFile = interfacesFile()

        val files = mutableListOf<File>()
        files.add(valueObjectsFile)
        interfacesFile?.let { files.add(it) }

        return Directory(
            name = dirName(),
            files = files
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

    private fun interfacesFile(): File? {
        if (module.interfaces.isEmpty()) {
            return null
        }

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
        return viewTypeFactory.create(type, modules).name()
    }
}