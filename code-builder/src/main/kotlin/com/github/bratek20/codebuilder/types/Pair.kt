package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.*

fun pairType(first: TypeBuilder, second: TypeBuilder) = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.pairType(first.build(c), second.build(c))
    }
}

fun CodeBuilder.newPair(first: String, second: String): CodeBuilder {
    return linePart(c.lang.newPair(first, second))
}

class PairOperations(
    private val b: CodeBuilder,
    private val variableName: String
) {
    fun first() {
        b.linePart(b.c.lang.pairFirst(variableName))
    }

    fun second() {
        b.linePart(b.c.lang.pairSecond(variableName))
    }
}

fun CodeBuilder.pairOp(variableName: String): PairOperations {
    return PairOperations(this, variableName)
}