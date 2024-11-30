package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.utils.camelToPascalCase

fun emptyLambda() = expression { c ->
    if (c.lang is CSharp) {
        "() => {}"
    } else {
        "{}"
    }
}

fun lambdaType(type: TypeBuilder) = typeName { c ->
    "Action<${type.build(c)}>"
}

class LambdaCallBuilder: CallBuilder() {
    lateinit var name: String

    override fun getCallName(c: CodeBuilderContext): String {
        return "$name.Invoke"
    }
}

typealias LambdaCallBuilderOps = LambdaCallBuilder.() -> Unit
fun lambdaCall(block: LambdaCallBuilderOps) = LambdaCallBuilder().apply(block)
fun lambdaCallStatement(block: LambdaCallBuilderOps) = expressionToStatement {
    lambdaCall(block)
}