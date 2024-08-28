package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

open class TopLevelCodeBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBlockBuilder> = mutableListOf()
    protected fun addOp(op: CodeBlockBuilder) {
        ops.add(op)
    }

    protected open fun beforeOperations(): CodeBuilderOps = {}
    protected open fun afterOperations(): CodeBuilderOps = {}

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(beforeOperations())

        ops.forEachIndexed { idx, op ->
            add(op)
            if (idx != ops.size - 1) {
                emptyLine()
            }
        }

        add(afterOperations())
    }

    open fun addInterface(ops: InterfaceBuilderOps) {
        addOp(InterfaceBuilder().apply(ops))
    }

    open fun addClass(classBlock: ClassBuilderOps) {
        addOp(ClassBuilder().apply(classBlock))
    }

    open fun addFunction(function: FunctionBuilderOps) {
        addOp(FunctionBuilder().apply(function))
    }

    open fun addEnum(enumBlock: EnumBuilderOps) {
        addOp(EnumBuilder().apply(enumBlock))
    }

    fun addFunctionCall(functionCall: FunctionCallBuilderOps) {
        addOp(FunctionCallBuilder().apply(functionCall))
    }

    fun addEmptyLines(amount: Int) {
        for (i in 0 until amount) {
            addOp(noOpBlock())
        }
    }
}
typealias TopLevelCodeBuilderOps = TopLevelCodeBuilder.() -> Unit
fun CodeBuilder.file(block: TopLevelCodeBuilderOps) = add(TopLevelCodeBuilder().apply(block))