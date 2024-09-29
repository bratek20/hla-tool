package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

open class TopLevelCodeBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBuilderBuilder> = mutableListOf()
    private var noEmptyLineAfter = false
    protected fun addOp(op: CodeBuilderBuilder) {
        if (ops.isNotEmpty() && !noEmptyLineAfter) {
            ops.add(emptyLineBlock())
        }
        ops.add(op)
        noEmptyLineAfter = false
    }
    private fun addOpNoEmptyLineAfter(op: CodeBlockBuilder) {
        addOp(op)
        noEmptyLineAfter = true
    }

    protected open fun beforeOperations(): CodeBuilderOps = {}
    protected open fun afterOperations(): CodeBuilderOps = {}

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(beforeOperations())

        ops.forEach {
            add(it)
        }

        addOps(afterOperations())
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

    fun addFunctionCall(ops: FunctionCallBuilderOps) {
        addOp(functionCallStatement(ops))
    }

    fun addExtraEmptyLines(amount: Int) {
        for (i in 0 until amount) {
            addOp(noOpBlock())
        }
    }

    fun addComment(comment: StringProvider) {
        addOpNoEmptyLineAfter(lineBlock("// ${comment()}"))
    }
}
typealias TopLevelCodeBuilderOps = TopLevelCodeBuilder.() -> Unit
fun CodeBuilder.file(block: TopLevelCodeBuilderOps) = add(TopLevelCodeBuilder().apply(block))