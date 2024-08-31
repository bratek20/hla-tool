package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiType
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.api.BaseApiType
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.directory.api.FileContent

data class ArgumentView(
    val name: String,
    val type: String,
    val apiType: ApiType
)
data class MethodView(
    val name: String,
    val returnType: String,
    val returnApiType: ApiType,
    val args: List<ArgumentView>,
    val throws: List<String>,
) {
    fun hasReturnValue(): Boolean {
        return returnApiType !is BaseApiType || returnApiType.name != BaseType.VOID
    }

    fun declaration(): String {
        val returnSuffix = ": $returnType"
        return "${name}(${argsDeclaration()})$returnSuffix"
    }

    fun declarationCB(): ProcedureSignatureBuilderOps = {
        name = this@MethodView.name
        args.forEach {
            addArg {
                name = it.name
                type = it.apiType.builder()
            }
        }
        returnType = this@MethodView.returnApiType.builder()
        this@MethodView.throws.forEach {
            addThrows(it)
        }
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

    //TODO remove when complex structure for web requests is added
    private fun hackedGetter(name: String): String {
        return "get" + camelToPascalCase(name) + "()"
    }
    fun argsGetPassWithPrefix(prefix: String): String {
        return args.joinToString(", ") { prefix + hackedGetter(it.name) }
    }
}

data class InterfaceView(
    val name: String,
    val methods: List<MethodView>
) {
    fun declarationCB(): InterfaceBuilderOps = {
        name = this@InterfaceView.name
        methods.forEach {
            addMethod(it.declarationCB())
        }
    }
}

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

class InterfacesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Interfaces
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

    override fun shouldGenerate(): Boolean {
        return module.getInterfaces().isNotEmpty()
    }

    override fun supportsCodeBuilder(): Boolean {
        return lang is CSharp
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val factory = InterfaceViewFactory(apiTypeFactory)

        val interfaces = factory.create(module.getInterfaces())

        interfaces.forEach {
            addInterface(it.declarationCB())
        }
    }
}