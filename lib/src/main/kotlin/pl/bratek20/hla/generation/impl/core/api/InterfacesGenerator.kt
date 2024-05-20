package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.InterfaceDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.FileGenerator

data class ArgumentView(
    val name: String,
    val type: String
)
data class MethodView(
    val name: String,
    val returnType: String?,
    private val args: List<ArgumentView>,
    val throws: List<String>,
) {
    // used by velocity
    fun argsDeclaration(): String {
        return args.joinToString(", ") { "${it.name}: ${it.type}" }
    }

    // used by velocity
    fun hasArgs(): Boolean {
        return args.isNotEmpty()
    }

    // used by velocity
    fun argsPass(): String {
        return args.joinToString(", ") { it.name }
    }
}

data class InterfaceView(
    val name: String,
    val methods: List<MethodView>
)

class InterfaceViewFactory(
    private val apiTypeFactory: ApiTypeFactory
) {
    fun create(definitions: List<InterfaceDefinition>): List<InterfaceView> {
        return definitions.map { create(it) }
    }

    fun create(definition: InterfaceDefinition): InterfaceView {
        return InterfaceView(
            name = definition.name,
            methods = definition.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = toViewType(method.returnType),
                    args = method.args.map { ArgumentView(it.name, toViewType(it.type)) },
                    throws = method.throws.map { it.name }
                )
            }
        )
    }

    private fun toViewType(type: TypeDefinition?): String {
        return apiTypeFactory.create(type).name()
    }
}

class InterfacesGenerator: FileGenerator() {
    override fun name(): String {
        return "Interfaces"
    }

    override fun generateFileContent(): FileContent?{
        if (module.interfaces.isEmpty()) {
            return null
        }

        val factory = InterfaceViewFactory(apiTypeFactory)

        return contentBuilder("interfaces.vm")
            .put("interfaces", factory.create(module.interfaces))
            .build()
    }


}