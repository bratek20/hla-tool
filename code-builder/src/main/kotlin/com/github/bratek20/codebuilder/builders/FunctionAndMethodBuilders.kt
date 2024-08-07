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
            lineSoftStart("$name: ")
            add(type)
            defaultValue?.let {
                linePart(" = $it")
            }
        }
    }
}
typealias ArgumentBuilderOps = ArgumentBuilder.() -> Unit
fun CodeBuilder.argument(block: ArgumentBuilderOps) = add(ArgumentBuilder().apply(block))

class ArgumentListBuilder: CodeBlockBuilder {
    private val args: MutableList<ArgumentBuilder> = mutableListOf()
    fun add(block: ArgumentBuilderOps) {
        args.add(ArgumentBuilder().apply(block))
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

open class MethodBuilder: MethodOrFunctionBuilder() {
    var override: Boolean = false
    override fun beforeName(c: CodeBuilderContext): String {
        val overridePart = if (override) "override " else ""
        return "${overridePart}${c.lang.methodDeclarationKeyword()}"
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
fun CodeBuilder.method(block: MethodBuilderOps) = add(MethodBuilder().apply(block))
fun method(block: MethodBuilderOps) = MethodBuilder().apply(block)

open class FunctionBuilder: MethodOrFunctionBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return c.lang.functionDeclarationKeyword()
    }
}
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit
fun function(block: FunctionBuilderOps) = FunctionBuilder().apply(block)
fun CodeBuilder.function(block: FunctionBuilderOps) = add(FunctionBuilder().apply(block))
