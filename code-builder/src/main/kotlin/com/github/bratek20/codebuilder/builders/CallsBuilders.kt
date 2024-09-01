package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.utils.camelToPascalCase

abstract class CallBuilder: ExpressionBuilder {
    protected abstract fun getCallName(c: CodeBuilderContext): String
    protected open fun beforeName(): String = ""

    private val args: MutableList<ExpressionBuilder> = mutableListOf()
    fun addArg(exp: ExpressionBuilderProvider) {
        args.add(exp())
    }

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        b.append(beforeName())

        b.append("${getCallName(c)}(")
        args.forEachIndexed { index, arg ->
            b.append(arg.build(c))
            if (index != args.size - 1) {
                b.append(", ")
            }
        }
        b.append(")")

        return b.toString()
    }
}

class MethodCallBuilder: CallBuilder() {
    lateinit var methodName: String

    var variableName: String? = null

    override fun getCallName(c: CodeBuilderContext): String {
        return if (c.lang.areMethodsPascalCase()) {
            camelToPascalCase(methodName)
        } else {
            methodName
        }
    }

    override fun beforeName(): String {
        return variableName?.let { "$it." } ?: ""
    }
}
typealias MethodCallBuilderOps = MethodCallBuilder.() -> Unit
fun methodCall(block: MethodCallBuilder.() -> Unit) = MethodCallBuilder().apply(block)

class FunctionCallBuilder: CallBuilder() {
    lateinit var name: String

    override fun getCallName(c: CodeBuilderContext): String {
        return name
    }
}
typealias FunctionCallBuilderOps = FunctionCallBuilder.() -> Unit
fun functionCall(block: FunctionCallBuilderOps) = FunctionCallBuilder().apply(block)

fun functionCallStatement(block: FunctionCallBuilderOps) = expressionToStatement {
    functionCall(block)
}

class ConstructorCallBuilder: CallBuilder() {
    lateinit var className: String

    override fun getCallName(c: CodeBuilderContext): String {
        return c.lang.constructorCall(className)
    }
}
fun constructorCall(block: ConstructorCallBuilder.() -> Unit) = ConstructorCallBuilder().apply(block)
fun CodeBuilder.legacyConstructorCall(block: ConstructorCallBuilder.() -> Unit) = add(ConstructorCallBuilder().apply(block))