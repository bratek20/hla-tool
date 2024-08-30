package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.ExpressionBuilderProvider
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun optionalType(elementType: TypeBuilder): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.listType(elementType.build(c))
    }
}

class OptionalOperations(
    private val variableName: String
) {
    fun get(): ExpressionBuilder = object : ExpressionBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            linePart(c.lang.optionalGet(variableName))
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
fun optionalOp(variableName: String): OptionalOperations {
    return OptionalOperations(variableName)
}