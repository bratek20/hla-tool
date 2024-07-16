package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.TypeScript

fun pairType(first: TypeBuilder, second: TypeBuilder) = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.pairType(first.build(c), second.build(c))
    }
}

class PairOperations(
    private val variableName: String
) {
    fun first(): CodeBuilderOps = {
        if (this.c.lang is TypeScript) {
            linePart("$variableName[0]")
        } else {
            linePart("$variableName.first")
        }
    }

    fun second(): CodeBuilderOps = {
        if (this.c.lang is TypeScript) {
            linePart("$variableName[1]")
        } else {
            linePart("$variableName.second")
        }
    }
}

fun pairOp(variableName: String): PairOperations {
    return PairOperations(variableName)
}