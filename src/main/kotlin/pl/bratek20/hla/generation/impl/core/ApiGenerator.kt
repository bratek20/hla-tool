package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.*
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

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
data class ValueObjectsView(
    val simpleList: List<SimpleValueObjectView>,
    val complexList: List<ComplexValueObjectView>
)

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

abstract class ApiGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val types: Types,
    private val domainFactory: OldDomainFactory = OldDomainFactory(module)
) {
    abstract fun dirName(): String

    abstract fun valueObjectsFileName(): String
    abstract fun valueObjectsContentBuilder(): VelocityFileContentBuilder

    abstract fun interfacesFileName(): String
    abstract fun interfacesContentBuilder(): VelocityFileContentBuilder

    fun generateCode(): Directory {
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
        val domainType = domainFactory.mapOptType(type)
        return types.map(domainType)
    }
}