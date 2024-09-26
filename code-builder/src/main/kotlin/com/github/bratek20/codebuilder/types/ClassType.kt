package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.expression

fun classType() = typeName("Type")

fun typeOf(type: TypeBuilder) = expression { c ->
    "typeof(${type.build(c)})"
}