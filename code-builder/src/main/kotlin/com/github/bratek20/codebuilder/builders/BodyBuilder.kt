package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class BodyBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBuilderBuilder> = mutableListOf()

    fun addFunctionCall(block: FunctionCallBuilderOps) {
        add(statement {{
            lineStart()
            add(functionCall(block))
            statementLineEnd()
        }})
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

