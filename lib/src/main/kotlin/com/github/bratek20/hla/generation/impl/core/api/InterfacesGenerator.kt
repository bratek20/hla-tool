package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

data class ArgumentView(
    val name: String,
    val type: String,
    val apiType: ApiType
)
data class MethodView(
    val name: String,
    val returnType: String?,
    val returnApiType: ApiType,
    val args: List<ArgumentView>,
    val throws: List<String>,
) {
    fun declaration(): String {
        val returnSuffix = if (returnType != null) ": $returnType" else ""
        return "${name}(${argsDeclaration()})$returnSuffix"
    }

    // used by velocity
    fun argsDeclaration(): String {
        return argsDeclarationWithPrefix("")
    }

    fun argsDeclarationWithPrefix(prefix: String): String {
        return args.joinToString(", ") { "$prefix${it.name}: ${it.type}" }
    }

    // used by velocity
    fun hasArgs(): Boolean {
        return args.isNotEmpty()
    }

    // used by velocity
    fun argsPass(): String {
        return argsPassWithPrefix("")
    }

    fun argsPassWithPrefix(prefix: String): String {
        return args.joinToString(", ") { prefix + it.name }
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
            name = definition.getName(),
            methods = definition.getMethods().map { method ->
                MethodView(
                    name = method.getName(),
                    returnType = toViewType(method.getReturnType()),
                    returnApiType = apiTypeFactory.create(method.getReturnType()),
                    args = method.getArgs().map { ArgumentView(it.getName(), toViewType(it.getType()), apiTypeFactory.create(it.getType())) },
                    throws = method.getThrows().map { it.getName() }
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
        if (module.getInterfaces().isEmpty()) {
            return null
        }

        val factory = InterfaceViewFactory(apiTypeFactory)

        return contentBuilder("interfaces.vm")
            .put("interfaces", factory.create(module.getInterfaces()))
            .build()
    }


}