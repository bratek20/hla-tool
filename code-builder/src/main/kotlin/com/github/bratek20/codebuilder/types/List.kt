package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.*

fun listType(elementType: TypeBuilder): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.listType(elementType.build(c))
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