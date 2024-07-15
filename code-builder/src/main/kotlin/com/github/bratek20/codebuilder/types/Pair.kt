package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.CodeBuilderOps
import com.github.bratek20.codebuilder.TypeScript

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

fun forPair(variableName: String): PairOperations {
    return PairOperations(variableName)
}