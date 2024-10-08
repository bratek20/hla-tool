package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.StatementBuilder
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun listType(elementType: TypeBuilder) = typeName { c ->
    c.lang.listType(elementType.build(c))
}

fun mutableListType(elementType: TypeBuilder) = typeName { c ->
    c.lang.mutableListType(elementType.build(c))
}

fun emptyMutableList(elementType: TypeBuilder) = expression { c ->
    c.lang.newEmptyMutableList(elementType.build(c))
}

fun newListOf(elementType: TypeBuilder, vararg elements: ExpressionBuilder) = expression { c ->
    val creation = emptyMutableList(elementType).build(c)
    val args = elements.joinToString(", ") { it.build(c) }
    "$creation { $args }"
}

class ListOperations(
    private val variableName: String
) {
    fun get(index: Int): ExpressionBuilder = expression("$variableName[$index]")

    fun add(element: ExpressionBuilderProvider): StatementBuilder = object : StatementBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            lineStart("${variableName}." + c.lang.listAddCallName())
            linePart("(")
            add(element())
            lineSoftEnd(")")
        }
    }

    fun find(predicate: ExpressionBuilderProvider) = expression { c ->
        StringBuilder().apply {
            append("${variableName}.${c.lang.listFindBegin()} it ${c.lang.lambdaArrow()} ")
            append(predicate().build(c))
            append(" ${c.lang.listFindEnd()}")
        }.toString()
    }

    fun map(predicate: ExpressionBuilderProvider) = expression { c ->
        StringBuilder().apply {
            append("${variableName}.${c.lang.listMapBegin()} it ${c.lang.lambdaArrow()} ")
            append(predicate().build(c))
            append(" " + c.lang.listMapEnd())
        }.toString()
    }
}

fun listOp(variableName: String): ListOperations {
    return ListOperations(variableName)
}