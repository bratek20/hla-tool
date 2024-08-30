package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.core.*

fun pairType(first: TypeBuilder, second: TypeBuilder) = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.pairType(first.build(c), second.build(c))
    }
}

fun CodeBuilder.legacyNewPair(first: String, second: String): CodeBuilder {
    return linePart(c.lang.newPair(first, second))
}

fun newPair(first: String, second: String): ExpressionBuilder = object : ExpressionBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(c.lang.newPair(first, second))
    }
}

class PairOperations(
    private val variableName: String
) {
    fun first(): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            linePart(c.lang.pairFirst(variableName))
        }
    }

    fun second() = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            linePart(c.lang.pairSecond(variableName))
        }
    }
}

fun pairOp(variableName: String): PairOperations {
    return PairOperations(variableName)
}