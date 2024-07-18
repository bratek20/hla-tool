package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.LinePartBuilder
import com.github.bratek20.codebuilder.core.linePartBlock

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
    private val variableName: String
) {
    fun get(index: Int): CodeBuilderOps = {
        linePart("$variableName[$index]")
    }

    fun add(element: String): CodeBuilderOps = {
        linePart(this.c.lang.listAdd(variableName, element))
    }
}
fun listOp(variableName: String): ListOperations {
    return ListOperations(variableName)
}