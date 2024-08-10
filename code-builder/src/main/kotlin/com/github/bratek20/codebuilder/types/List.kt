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

fun emptyMutableList(elementType: TypeBuilder): LinePartBuilder = object : LinePartBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.emptyMutableList(elementType.build(c))
    }
}

class ItOperations(
    b: CodeBuilder
) {
    val it = ItBuilder(b)
}
typealias ItOperationsOps = ItOperations.() -> Unit

class ItBuilder(
    private val b: CodeBuilder
) {
    val name = "it"

    fun isEqualTo(other: CodeBuilderOps) {
        b.linePart("it == ")
        b.add(other)
    }
}

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
        b.lineSoftEnd(")")
    }

    fun find(predicate: ItOperationsOps) {
        val itOperations = ItOperations(b)

        b.linePart("${variableName}.${lang.listFindBegin()} it ${lang.lambdaArrow()} ")
        itOperations.apply(predicate)
        b.lineSoftEnd(" " + b.c.lang.listFindEnd())
    }

    fun map(predicate: ItOperationsOps) {
        val itOperations = ItOperations(b)

        b.linePart("${variableName}.${lang.listMapBegin()} it ${lang.lambdaArrow()} ")
        itOperations.apply(predicate)
        b.lineSoftEnd(" " + b.c.lang.listMapEnd())
    }
}

fun CodeBuilder.listOp(variableName: String): ListOperations {
    return ListOperations(this, variableName)
}