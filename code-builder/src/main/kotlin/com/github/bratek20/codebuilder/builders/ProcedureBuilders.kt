package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.utils.camelToPascalCase

class ArgumentBuilder: ExpressionBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var defaultValue: ExpressionBuilder? = null

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        if (c.lang is CSharp) {
            b.append("${type.build(c)} $name")
        }
        else {
            b.append("$name: ${type.build(c)}")
        }

        defaultValue?.let {
            b.append(" = ${it.build(c)}")
        }

        return b.toString()
    }
}
typealias ArgumentBuilderOps = ArgumentBuilder.() -> Unit
fun argument(block: ArgumentBuilderOps) = ArgumentBuilder().apply(block)

//TODO-CREATENAMED: idea: make ArgumentBuilder open, and create a subclass that takes ArgumentBuilder list as input and can build a deconstructed argument?

class ArgumentListBuilder: ExpressionBuilder {
    private val args: MutableList<ArgumentBuilder> = mutableListOf()
    fun add(block: ArgumentBuilderOps) {
        args.add(ArgumentBuilder().apply(block))
    }

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
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

abstract class ProcedureSignatureBuilder: CodeBlockBuilder {
    lateinit var name: String

    var returnType: TypeBuilder? = null

    var comment: String? = null

    protected val args: ArgumentListBuilder = ArgumentListBuilder()
    fun addArg(ops: ArgumentBuilderOps) {
        args.add(ops)
    }

    private val generics: MutableList<String> = mutableListOf()
    fun addGeneric(typeName: String) {
        generics.add(typeName)
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
            lineStart(beforeName(c))
            returnType?.let {
                add(it)
                linePart(" ")
            } ?: linePart("void ")

            linePart(camelToPascalCase(name))

            generics.forEach { generic ->
                linePart("<$generic>")
            }

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
typealias ProcedureSignatureBuilderOps = ProcedureSignatureBuilder.() -> Unit

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
    fun setBody(block: BodyBuilderOps) {
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

class MethodBuilder: ProcedureBuilder() {
    var overridesClassMethod: Boolean = false
    var static: Boolean = false
    var modifier: AccessModifier? = null

    override fun beforeName(c: CodeBuilderContext): String {
        val overridePart = if (overridesClassMethod) "override " else ""
        val staticPart = if (static && c.lang.supportsStaticKeyword()) "static " else ""
        val modifierPart = modifier?.let { "${it.name.lowercase()} " } ?: c.lang.defaultTopLevelAccessor()
        return "${modifierPart}${staticPart}${overridePart}${c.lang.methodDeclarationKeyword()}"
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