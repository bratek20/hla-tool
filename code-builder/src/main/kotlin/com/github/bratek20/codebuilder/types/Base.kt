package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.BaseType
import com.github.bratek20.codebuilder.CodeBuilderContext

fun baseType(value: BaseType) = object: TypeBuilder {
    override fun build(c: CodeBuilderContext): String {
        return c.lang.mapBaseType(value)
    }
}