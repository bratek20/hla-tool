package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

abstract class CallBuilder: CodeBlockBuilder {
    protected abstract fun getCallName(): String
    protected open fun beforeName(): String = ""

    private val args: MutableList<CodeBuilderOps> = mutableListOf()
    fun addArg(ops: CodeBuilderOps) {
        args.add(ops)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart(beforeName())

        linePart("${getCallName()}(")
        args.forEachIndexed { index, arg ->
            add(arg)
            if (index != args.size - 1) {
                linePart(", ")
            }
        }
        lineSoftEnd(")")
    }
}

class MethodCallBuilder: CallBuilder() {
    lateinit var methodName: String

    var variableName: String? = null

    override fun getCallName(): String {
        return methodName
    }

    override fun beforeName(): String {
        return variableName?.let { "$it." } ?: ""
    }
}
fun CodeBuilder.methodCall(block: MethodCallBuilder.() -> Unit) = add(MethodCallBuilder().apply(block))

class FunctionCallBuilder: CallBuilder() {
    lateinit var name: String

    override fun getCallName(): String {
        return name
    }
}
fun CodeBuilder.functionCall(block: FunctionCallBuilder.() -> Unit) = add(FunctionCallBuilder().apply(block))

class ClassConstructorCall: CallBuilder() {
    lateinit var className: String

    override fun getCallName(): String {
        return className
    }
}
fun CodeBuilder.classConstructorCall(block: ClassConstructorCall.() -> Unit) = add(ClassConstructorCall().apply(block))