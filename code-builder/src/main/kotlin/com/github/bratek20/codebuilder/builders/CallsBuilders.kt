package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

abstract class CallBuilder: ExpressionBuilder {
    protected abstract fun getCallName(c: CodeBuilderContext): String
    protected open fun beforeName(): String = ""

    // hacky solution to make work methodCall() + methodCall() example
    var skipSoftEnd: Boolean? = null

    private val args: MutableList<CodeBuilderOps> = mutableListOf()

    @Deprecated("Use addArg instead", ReplaceWith("addArg"))
    fun addArgLegacy(ops: CodeBuilderOps) {
        args.add(ops)
    }

    fun addArg(exp: ExpressionBuilder) {
        args.add {
            add(exp)
        }
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart(beforeName())

        linePart("${getCallName(c)}(")
        args.forEachIndexed { index, arg ->
            add(arg)
            if (index != args.size - 1) {
                linePart(", ")
            }
        }
        linePart(")")
        if (skipSoftEnd != true) {
            lineSoftEnd()
        }
    }
}

class MethodCallBuilder: CallBuilder() {
    lateinit var methodName: String

    var variableName: String? = null

    override fun getCallName(c: CodeBuilderContext): String {
        return methodName
    }

    override fun beforeName(): String {
        return variableName?.let { "$it." } ?: ""
    }
}
fun methodCall(block: MethodCallBuilder.() -> Unit) = MethodCallBuilder().apply(block)

class FunctionCallBuilder: CallBuilder() {
    lateinit var name: String

    override fun getCallName(c: CodeBuilderContext): String {
        return name
    }
}
typealias FunctionCallBuilderOps = FunctionCallBuilder.() -> Unit
fun CodeBuilder.legacyFunctionCall(block: FunctionCallBuilder.() -> Unit) = add(FunctionCallBuilder().apply(block))
fun functionCall(block: FunctionCallBuilderOps) = FunctionCallBuilder().apply(block)

class ConstructorCallBuilder: CallBuilder() {
    lateinit var className: String

    override fun getCallName(c: CodeBuilderContext): String {
        return c.lang.constructorCall(className)
    }
}
fun CodeBuilder.constructorCall(block: ConstructorCallBuilder.() -> Unit) = add(ConstructorCallBuilder().apply(block))