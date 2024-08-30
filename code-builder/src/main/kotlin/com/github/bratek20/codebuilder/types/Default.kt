package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.LinePartBuilder

interface TypeBuilder: LinePartBuilder

fun typeName(value: String): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return value
    }
}