package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.utils.camelToPascalCase

abstract class CallBuilder: ExpressionBuilder {
    protected abstract fun getCallName(c: CodeBuilderContext): String
    protected open fun beforeName(c: CodeBuilderContext): String = ""

    private val args: MutableList<ExpressionBuilder> = mutableListOf()
    fun addArg(exp: ExpressionBuilderProvider) {
        args.add(exp())
    }

    private val generics: MutableList<String> = mutableListOf()
    fun addGeneric(name: String) {
        generics.add(name)
    }

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        b.append(beforeName(c))

        b.append(getCallName(c))
        if(generics.isNotEmpty()) {
            b.append("<")
            generics.forEachIndexed { index, generic ->
                b.append(generic)
                if (index != generics.size - 1) {
                    b.append(", ")
                }
            }
            b.append(">")
        }
        b.append("(")
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

    var target: ExpressionBuilder? = null

    override fun getCallName(c: CodeBuilderContext): String {
        return if (c.lang.areMethodsPascalCase()) {
            camelToPascalCase(methodName)
        } else {
            methodName
        }
    }

    override fun beforeName(c: CodeBuilderContext): String {
        return target?.let { "${it.build(c)}." } ?: ""
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