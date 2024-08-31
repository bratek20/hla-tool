package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

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
    fun get(): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            linePart(c.lang.optionalGet(variableName))
        }
    }

    fun orElse(defaultValue: ExpressionBuilderProvider) = expression { c ->
        c.lang.optionalOrElse(variableName, defaultValue().getValue(c))
    }

    fun map(predicate: ExpressionBuilderProvider): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            lineSoftStart("${variableName}.${c.lang.optionalMapBegin()} it ${c.lang.lambdaArrow()} ")
            add(predicate())
            lineSoftEnd(" " + c.lang.optionalMapEnd())
        }
    }
}
fun optionalOp(variableName: String): OptionalOperations {
    return OptionalOperations(variableName)
}