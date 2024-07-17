package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.LinePartBuilder

fun listType(elementType: TypeBuilder): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.listType(elementType.build(c))
    }
}

fun mutableListType(elementType: TypeBuilder): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.mutableListType(elementType.build(c))
    }
}

fun emptyMutableList(): LinePartBuilder = object : LinePartBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.emptyMutableList()
    }
}

class ListOperations(
    private val b: CodeBuilder,
    private val variableName: String
) {
    fun get(index: Int) {
        b.linePart("$variableName[$index]")
    }

    fun add(element: CodeBuilderOps) {
        b.linePart(b.c.lang.listAddCall(variableName))
        b.linePart("(")
        b.add(element)
        b.linePart(")")
    }
}

fun CodeBuilder.listOp(variableName: String): ListOperations {
    return ListOperations(this, variableName)
}