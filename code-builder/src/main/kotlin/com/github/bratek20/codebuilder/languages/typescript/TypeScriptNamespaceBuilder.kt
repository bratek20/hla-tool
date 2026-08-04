package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

class NamespaceClassBuilder: ClassBuilder() {
    override fun beforeClassKeyword(): String = "export "
}

class NamespaceFunctionBuilder: FunctionBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return "export " + super.beforeName(c)
    }
}

class NamespaceInterfaceBuilder: InterfaceBuilder() {
    override fun beforeInterfaceKeyword(): String = "export "
}

class NamespaceVariableBuilder: CodeBlockBuilder {
    lateinit var name: String

    var type: TypeBuilder? = null
    var mutable: Boolean = false
    var value: ExpressionBuilder? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val keyword = if (mutable) "let" else "const"
        lineStart("export $keyword $name")
        type?.let {
            linePart(": ")
            add(it)
        }
        value?.let {
            linePart(" = ")
            add(it)
        }
        lineEnd()
    }
}
typealias NamespaceVariableBuilderOps = NamespaceVariableBuilder.() -> Unit

class ConstBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var value: CodeBuilderOps

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart("export const $name = ")
        addOps(value)
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

    override fun addInterface(ops: InterfaceBuilderOps) {
        addOp(NamespaceInterfaceBuilder().apply(ops))
    }

    fun addConst(block: ConstBuilderOps) {
        addOp(ConstBuilder().apply(block))
    }

    fun addVariable(block: NamespaceVariableBuilderOps) {
        addOp(NamespaceVariableBuilder().apply(block))
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
fun typeScriptNamespace(block: TypeScriptNamespaceBuilderOps) = TypeScriptNamespaceBuilder().apply(block)