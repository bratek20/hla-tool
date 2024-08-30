package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

class ArgumentBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var defaultValue: String? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        return {
            if (c.lang is CSharp) {
                lineSoftStart()
                add(type)
                linePart(" $name")
            }
            else {
                lineSoftStart("$name: ")
                add(type)
            }

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

abstract class ProcedureSignatureBuilder: CodeBlockBuilder {
    lateinit var name: String

    var returnType: TypeBuilder? = null

    var comment: String? = null

    protected val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(ops: ArgumentBuilderOps) {
        args.add(ops)
    }

    private val throws: MutableList<String> = mutableListOf()
    fun addThrows(exceptionName: String) {
        throws.add(exceptionName)
    }

    protected abstract fun beforeName(c: CodeBuilderContext): String

    private fun throwsOps(): CodeBuilderOps = {
        if (throws.isNotEmpty()) {
            when (c.lang) {
                is Kotlin -> {
                    line("@Throws(")
                    tab()
                    throws.forEach { exceptionName ->
                        line("$exceptionName::class,")
                    }
                    untab()
                    line(")")
                }
                is TypeScript -> {
                    line("/**")
                    throws.forEach { exceptionName ->
                        line("* @throws { $exceptionName }")
                    }
                    line("*/")
                }
                is CSharp -> {
                    throws.forEach { exceptionName ->
                        line("/// <exception cref=\"$exceptionName\"/>")
                    }
                }
            }
        }
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        comment?.let {
            line("// $it")
        }
        addOps(throwsOps())
        if (c.lang is CSharp) {
            lineStart()
            returnType?.let {
                add(it)
                linePart(" ")
            } ?: linePart("void ")
            linePart(name)
            add(args)
        }
        else {
            lineStart("${beforeName(c)}$name")
            add(args)
            returnType?.let {
                linePart(": ")
                add(it)
            }
        }
    }
}

class InterfaceMethodBuilder: ProcedureSignatureBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return c.lang.methodDeclarationKeyword()
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(super.getOperations(c))
        statementLineEnd()
    }

    companion object {
        fun create(block: InterfaceMethodBuilderOps): InterfaceMethodBuilder {
            return InterfaceMethodBuilder().apply(block)
        }
    }
}
typealias InterfaceMethodBuilderOps = InterfaceMethodBuilder.() -> Unit



abstract class ProcedureBuilder: ProcedureSignatureBuilder() {
    var legacyBody: CodeBuilderOps? = null

    private var body: BodyBuilderOps? = null
    fun body(block: BodyBuilderOps) {
        body = block
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(super.getOperations(c))

        linePart(" {")
        tab()
        legacyBody?.let { addOps(it) }
        body?.let { add(BodyBuilder().apply(it)) }
        untab()
        line("}")
    }
}

open class MethodBuilder: ProcedureBuilder() {
    var override: Boolean = false
    override fun beforeName(c: CodeBuilderContext): String {
        val overridePart = if (override) "override " else ""
        return "${overridePart}${c.lang.methodDeclarationKeyword()}"
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
fun CodeBuilder.legacyMethod(block: MethodBuilderOps) = add(MethodBuilder().apply(block))
fun method(block: MethodBuilderOps) = MethodBuilder().apply(block)

open class FunctionBuilder: ProcedureBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return c.lang.functionDeclarationKeyword()
    }
}
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit
fun function(block: FunctionBuilderOps) = FunctionBuilder().apply(block)
fun CodeBuilder.legacyFunction(block: FunctionBuilderOps) = add(FunctionBuilder().apply(block))
