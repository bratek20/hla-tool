package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class BodyBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBuilderBuilder> = mutableListOf()

    fun add(block: StatementBuilder) {
        ops.add(block)
    }

    fun addMany(blocks: List<StatementBuilder>) {
        blocks.forEach { add(it) }
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        ops.forEach {
            add(it)
        }
    }
}
typealias BodyBuilderOps = BodyBuilder.() -> Unit

