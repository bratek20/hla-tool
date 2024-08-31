package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.StatementBuilder
import com.github.bratek20.codebuilder.builders.expression
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

fun emptyMutableList(elementType: TypeBuilder) = expression { c ->
    c.lang.emptyMutableList(elementType.build(c))
}

class ItOpsBuilder: ExpressionBuilder {
    val name = "it"

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {

    }
}
typealias ItOpsBuilderOps = ItOpsBuilder.() -> Unit
fun itOps(block: ItOpsBuilderOps) = ItOpsBuilder().apply(block)

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

    fun find(predicate: ExpressionBuilderProvider): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            lineSoftStart("${variableName}.${c.lang.listFindBegin()} it ${c.lang.lambdaArrow()} ")
            add(predicate())
            lineSoftEnd(" " + c.lang.listFindEnd())
        }
    }

    fun map(predicate: ExpressionBuilderProvider): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            lineSoftStart("${variableName}.${c.lang.listMapBegin()} it ${c.lang.lambdaArrow()} ")
            add(predicate())
            lineSoftEnd(" " + c.lang.listMapEnd())
        }
    }
}

fun listOp(variableName: String): ListOperations {
    return ListOperations(variableName)
}