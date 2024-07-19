package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.types.TypeBuilder

class ArgumentBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var defaultValue: String? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        return {
            linePart("$name: ")
            add(type)
            defaultValue?.let {
                linePart(" = $it")
            }
        }
    }
}
typealias ArgumentBuilderOps = ArgumentBuilder.() -> Unit
fun argument(block: ArgumentBuilderOps) = ArgumentBuilder().apply(block)

class ArgumentListBuilder: CodeBlockBuilder {
    private val args: MutableList<ArgumentBuilder> = mutableListOf()
    fun add(block: ArgumentBuilderOps) {
        args.add(argument(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart("(")
        args.forEachIndexed { index, arg ->
            add(arg)
            if (index != args.size - 1) {
                linePart(", ")
            }
        }
        linePart(")")
    }
}

abstract class MethodOrFunctionBuilder: CodeBlockBuilder {
    lateinit var name: String

    var returnType: TypeBuilder? = null
    var body: CodeBuilderOps? = null

    protected val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(ops: ArgumentBuilderOps) {
        args.add(ops)
    }

    protected abstract fun beforeName(c: CodeBuilderContext): String

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart("${beforeName(c)}$name")
        add(args)
        returnType?.let {
            linePart(": ")
            add(it)
        }
        linePart(" {")

        tab()
        body?.let { add(it) }
        untab()

        line("}")
    }
}

class MethodBuilder: MethodOrFunctionBuilder() {
    var override: Boolean = false
    override fun beforeName(c: CodeBuilderContext): String {
        val overridePart = if (override) "override " else ""
        return "${overridePart}${c.lang.methodDeclarationKeyword()}"
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
fun CodeBuilder.method(block: MethodBuilderOps): CodeBuilder {
    add(MethodBuilder().apply(block))
    return this
}


class FunctionBuilder: MethodOrFunctionBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return c.lang.functionDeclarationKeyword()
    }
}
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit
fun CodeBuilder.function(block: FunctionBuilderOps): CodeBuilder {
    add(FunctionBuilder().apply(block))
    return this
}

abstract class MethodOrFunctionCallBuilder: CodeBlockBuilder {
    protected abstract fun getCallName(): String
    protected abstract fun beforeName(): String

    private val args: MutableList<CodeBuilderOps> = mutableListOf()
    fun addArg(ops: CodeBuilderOps) {
        args.add(ops)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart()

        linePart(beforeName())
        linePart("${getCallName()}(")
        args.forEachIndexed { index, arg ->
            add(arg)
            if (index != args.size - 1) {
                linePart(", ")
            }
        }
        linePart(")")

        lineEnd()
    }
}

class MethodCallBuilder: MethodOrFunctionCallBuilder() {
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

class FunctionCallBuilder: MethodOrFunctionCallBuilder() {
    lateinit var name: String

    override fun getCallName(): String {
        return name
    }

    override fun beforeName(): String {
        return ""
    }
}
fun CodeBuilder.functionCall(block: FunctionCallBuilder.() -> Unit) = add(FunctionCallBuilder().apply(block))