package com.github.bratek20.codebuilder.typescript

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

class NamespaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun classBlock(block: ClassBuilderOps) {
        body.add(NamespaceClassBuilder().apply(block))
    }

    fun function(block: FunctionBuilderOps) {
        body.add(NamespaceFunctionBuilder().apply(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("namespace $name {")
        tab()
        body.forEach { add(it) }
        untab()
        line("}")
    }
}
typealias NamespaceBuilderOps = NamespaceBuilder.() -> Unit
fun CodeBuilder.namespace(block: NamespaceBuilderOps) = add(NamespaceBuilder().apply(block))