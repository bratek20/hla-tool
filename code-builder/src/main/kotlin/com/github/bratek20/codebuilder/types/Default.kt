package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.LinePartBuilder

interface TypeBuilder: LinePartBuilder
typealias TypeBuilderProvider = () -> TypeBuilder

fun typeName(value: String): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return value
    }
}

fun typeName(valueProvider: (CodeBuilderContext) -> String): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return valueProvider(c)
    }
}