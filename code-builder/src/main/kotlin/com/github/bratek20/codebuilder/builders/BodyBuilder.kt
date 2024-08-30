package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class BodyBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBlockBuilder> = mutableListOf()

    fun addFunctionCall(block: FunctionCallBuilderOps) {
        ops.add(FunctionCallBuilder().apply(block))
    }

    fun add(block: StatementBuilder) {
        ops.add(block)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        ops.forEach {
            add(it)
        }
    }
}
typealias BodyBuilderOps = BodyBuilder.() -> Unit

