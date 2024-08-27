package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*

class NamespaceClassBuilder: ClassBuilder() {
    override fun beforeClassKeyword(): String = "export "
}

class NamespaceFunctionBuilder: FunctionBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return "export " + super.beforeName(c)
    }
}

class ConstBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var value: CodeBuilderOps

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart("export const $name = ")
        add(value)
        lineSoftEnd()
    }
}
typealias ConstBuilderOps = ConstBuilder.() -> Unit

class TypeScriptNamespaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun addClass(block: ClassBuilderOps) {
        body.add(NamespaceClassBuilder().apply(block))
    }

    fun addFunction(block: FunctionBuilderOps) {
        body.add(NamespaceFunctionBuilder().apply(block))
    }

    fun addConst(block: ConstBuilderOps) {
        body.add(ConstBuilder().apply(block))
    }

    fun addEnum(block: EnumBuilderOps) {
        body.add(EnumBuilder().apply(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("namespace $name {")
        tab()
        body.forEach { add(it) }
        untab()
        line("}")
    }
}
typealias TypeScriptNamespaceBuilderOps = TypeScriptNamespaceBuilder.() -> Unit
fun CodeBuilder.namespace(block: TypeScriptNamespaceBuilderOps) = add(TypeScriptNamespaceBuilder().apply(block))