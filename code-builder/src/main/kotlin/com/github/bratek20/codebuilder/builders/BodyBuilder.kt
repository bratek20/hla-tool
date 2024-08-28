package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class BodyBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBlockBuilder> = mutableListOf()

    fun addFunctionCall(block: FunctionCallBuilderOps) {
        ops.add(FunctionCallBuilder().apply(block))
    }

    fun addVariableAssignment(block: VariableAssignmentBuilderOps) {
        ops.add(VariableAssignmentBuilder().apply(block))
    }

    fun addReturn(block: ExpressionBuilder) {
        ops.add(ReturnBuilder(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        ops.forEach {
            add(it)
        }
    }
}
typealias BodyBuilderOps = BodyBuilder.() -> Unit

