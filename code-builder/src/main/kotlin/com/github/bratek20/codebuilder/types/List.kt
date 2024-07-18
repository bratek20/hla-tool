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

class ItBuilder(
    private val b: CodeBuilder
) {
    fun it(ops: CodeBuilderOps) {
        b.add(ops)
    }

    fun isEqualsTo(other: CodeBuilderOps) {
        b.linePart("it == ")
        b.add(other)
    }
}
typealias ItOps = ItBuilder.() -> Unit

class ListOperations(
    private val b: CodeBuilder,
    private val variableName: String
) {
    private val lang = b.c.lang

    fun get(index: Int) {
        b.linePart("$variableName[$index]")
    }

    fun add(element: CodeBuilderOps) {
        b.lineStart("${variableName}." + lang.listAddCallName())
        b.linePart("(")
        b.add(element)
        b.lineEnd(")")
    }

    fun find(predicate: ItOps) {
        b.lineStart("${variableName}.${lang.listFindBegin()} it ${lang.lambdaArrow()} ")
        ItBuilder(b).apply(predicate)
        b.lineEnd(" " + b.c.lang.listFindEnd())
    }
}

fun CodeBuilder.listOp(variableName: String): ListOperations {
    return ListOperations(this, variableName)
}