package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.StatementBuilder
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun listType(elementType: TypeBuilder) = typeName { c ->
    c.lang.listType(elementType.build(c))
}

fun mutableListType(elementType: TypeBuilder) = typeName { c ->
    c.lang.mutableListType(elementType.build(c))
}

//emptyList collides with kotlin global function
fun emptyImmutableList(elementType: TypeBuilder) = expression { c ->
    c.lang.newEmptyList(elementType.build(c))
}

fun emptyMutableList(elementType: TypeBuilder) = expression { c ->
    c.lang.newEmptyMutableList(elementType.build(c))
}

fun newListOf(elementType: TypeBuilder, vararg elements: ExpressionBuilder) = expression { c ->
    if (c.lang is CSharp) {
        val creation = emptyMutableList(elementType).build(c)
        val args = elements.joinToString(", ") { it.build(c) }
        "$creation { $args }"
    }
    else {
        val args = elements.joinToString(", ") { it.build(c) }
        "[ $args ]"
    }
}

class ListOperations(
    private val variable: ExpressionBuilder
) {
    fun get(index: Int): ExpressionBuilder = expression { c ->
        "${variable.build(c)}[$index]"
    }

    fun add(element: ExpressionBuilderProvider): StatementBuilder = object : StatementBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            lineStart("${variable.build(c)}." + c.lang.listAddCallName())
            linePart("(")
            add(element())
            lineSoftEnd(")")
        }
    }

    fun find(predicate: ExpressionBuilderProvider) = expression { c ->
        StringBuilder().apply {
            append("${variable.build(c)}.${c.lang.listFindBegin()} it ${c.lang.lambdaArrow()} ")
            append(predicate().build(c))
            append(" ${c.lang.listFindEnd()}")
        }.toString()
    }

    fun map(predicate: ExpressionBuilderProvider) = expression { c ->
        StringBuilder().apply {
            append("${variable.build(c)}.${c.lang.listMapBegin()} it ${c.lang.lambdaArrow()} ")
            append(predicate().build(c))
            append(" " + c.lang.listMapEnd())
        }.toString()
    }
}

fun listOp(variable: ExpressionBuilder): ListOperations {
    return ListOperations(variable)
}