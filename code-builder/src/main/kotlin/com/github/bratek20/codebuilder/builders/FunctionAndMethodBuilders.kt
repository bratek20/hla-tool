package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.types.TypeBuilder

class ArgumentBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        return {
            linePart("$name: ")
            add(type)
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

class MethodBuilder: CodeBlockBuilder {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilder? = null
    var body: CodeBuilderOps? = null

    private val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(ops: ArgumentBuilderOps) {
        args.add(ops)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val overridePart = if (override) "override " else ""

        linePart("${overridePart}${c.lang.methodDeclarationKeyword()}$name")
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
typealias MethodBuilderOps = MethodBuilder.() -> Unit
fun method(block: MethodBuilderOps) = MethodBuilder().apply(block)

class FunctionBuilder: CodeBlockBuilder {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilder? = null
    var body: CodeBuilderOps? = null

    private val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(ops: ArgumentBuilderOps) {
        args.add(ops)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart("${c.lang.functionDeclarationKeyword()}$name")
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
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit
fun function(block: FunctionBuilderOps) = FunctionBuilder().apply(block)