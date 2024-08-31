package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.core.Kotlin

fun softOptionalType(elementType: TypeBuilder) = typeName {
    c -> c.lang.softOptionalType(elementType.build(c))
}
fun hardOptionalType(elementType: TypeBuilder) = typeName {
    c -> c.lang.hardOptionalType(elementType.build(c))
}

fun softOptional(variableName: String) = expression {
    variableName
}

fun hardOptional(elementType: TypeBuilder, variableName: String) = expression {
    c -> c.lang.newHardOptional(elementType.build(c), variableName)
}

class OptionalOperations(
    private val variableName: String
) {
    fun get() = expression { c ->
        c.lang.optionalGet(variableName)
    }

    fun orElse(defaultValueProvider: ExpressionBuilderProvider) = expression { c ->
        val defaultValue = defaultValueProvider().build(c)

        if (defaultValue == "null" && c.lang is Kotlin) {
            variableName
        }
        else {
            c.lang.optionalOrElse(variableName, defaultValue)
        }
    }

    fun map(predicate: ExpressionBuilderProvider) = expression { c ->
       StringBuilder().apply {
           append("${variableName}${c.lang.optionalMapBegin()} it ${c.lang.lambdaArrow()} ")
           append(predicate().build(c))
           append(" " + c.lang.optionalMapEnd())
       }.toString()
    }
}
fun optionalOp(variableName: String): OptionalOperations {
    return OptionalOperations(variableName)
}