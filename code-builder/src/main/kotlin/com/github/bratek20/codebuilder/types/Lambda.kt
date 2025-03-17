package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.utils.camelToPascalCase

fun emptyLambda(argsNumber: Int = 0) = expression { c ->
    if (c.lang is CSharp) {
        if (argsNumber == 0) {
            "() => {}"
        } else if (argsNumber == 1) {
            "(_) => {}"
        } else {
            "(" + (1..argsNumber).joinToString(", ") { "_$it" } + ") => {}"
        }
    } else {
        "{}"
    }
}

class LambdaBuilder: ExpressionBuilder {
    private val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(arg: ArgumentBuilderOps) {
        args.add(arg)
    }

    lateinit var body: ExpressionBuilder

    override fun build(c: CodeBuilderContext): String {
        return "${args.build(c)} => { " + body.build(c) + " }"
    }
}
fun lambda(block: LambdaBuilder.() -> Unit) = LambdaBuilder().apply(block)

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