package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.core.CodeBuilderContext
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

fun hardOptional(elementType: TypeBuilder, variable: ExpressionBuilderProvider) = expression {
    c -> c.lang.newHardOptional(elementType.build(c), variable().build(c))
}

fun emptyHardOptional(elementType: TypeBuilder) = expression {
    c -> c.lang.emptyHardOptional(elementType.build(c))
}

class OptionalOperations(
    private val variable: ExpressionBuilderProvider
) {
    private fun getVariableName(c: CodeBuilderContext) = variable().build(c)

    fun get() = expression { c ->
        c.lang.optionalGet(getVariableName(c))
    }

    fun orElse(defaultValueProvider: ExpressionBuilderProvider) = expression { c ->
        val defaultValue = defaultValueProvider().build(c)

        if (defaultValue == "null" && c.lang is Kotlin) {
            getVariableName(c)
        }
        else {
            c.lang.optionalOrElse(getVariableName(c), defaultValue)
        }
    }

    fun map(predicate: ExpressionBuilderProvider) = expression { c ->
       StringBuilder().apply {
           append("${getVariableName(c)}${c.lang.optionalMapBegin()} it ${c.lang.lambdaArrow()} ")
           append(predicate().build(c))
           append(" " + c.lang.optionalMapEnd())
       }.toString()
    }
}
fun optionalOp(variable: ExpressionBuilderProvider): OptionalOperations {
    return OptionalOperations(variable)
}
fun optionalOp(variable: ExpressionBuilder) = optionalOp {
    variable
}