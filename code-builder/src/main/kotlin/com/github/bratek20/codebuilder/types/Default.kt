package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.CodeBuilderContext
import com.github.bratek20.codebuilder.LinePartBuilder

interface TypeBuilder: LinePartBuilder

fun type(value: String): TypeBuilder = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return value
    }
}