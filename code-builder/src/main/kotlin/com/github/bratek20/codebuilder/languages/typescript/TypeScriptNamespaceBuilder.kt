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

class TypeScriptNamespaceBuilder: TopLevelCodeBuilder() {
    lateinit var name: String

    override fun addClass(block: ClassBuilderOps) {
        addOp(NamespaceClassBuilder().apply(block))
    }

    override fun addFunction(block: FunctionBuilderOps) {
        addOp(NamespaceFunctionBuilder().apply(block))
    }

    fun addConst(block: ConstBuilderOps) {
        addOp(ConstBuilder().apply(block))
    }

    override fun beforeOperations(): CodeBuilderOps {
        return {
            line("namespace $name {")
            tab()
        }
    }

    override fun afterOperations(): CodeBuilderOps {
        return {
            untab()
            line("}")
        }
    }
}
typealias TypeScriptNamespaceBuilderOps = TypeScriptNamespaceBuilder.() -> Unit
fun CodeBuilder.namespace(block: TypeScriptNamespaceBuilderOps) = add(TypeScriptNamespaceBuilder().apply(block))