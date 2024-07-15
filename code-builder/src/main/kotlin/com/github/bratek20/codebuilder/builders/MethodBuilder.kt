package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

fun pairFirst(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairFirst(variableName)
}

fun pairSecond(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairSecond(variableName)
}


interface TypeBuilder: CodeBlockBuilder

class PairTypeBuilder: TypeBuilder {
    lateinit var first: TypeBuilder
    lateinit var second: TypeBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(c.lang.pairTypeStart())
        add(first)
        linePart(", ")
        add(second)
        linePart(c.lang.pairTypeEnd())
    }
}
fun pairType(first: TypeBuilder, second: TypeBuilder) = PairTypeBuilder().apply {
    this.first = first
    this.second = second
}

class BaseTypeBuilder: TypeBuilder {
    lateinit var value: BaseType

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(c.lang.mapBaseType(value))
    }
}
fun baseType(value: BaseType) = BaseTypeBuilder().apply { this.value = value }

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

class BodyBuilder: CodeBlockBuilder {
    private val builderOps = mutableListOf<CodeBuilderOps>()

    fun line(value: String) {
        builderOps.add { line(value) }
    }

    fun linePart(value: String) {
        builderOps.add { linePart(value) }
    }

    fun pairFirst(variableName: String) {
        builderOps.add { linePart(pairFirst(variableName, this.c.lang)) }
    }

    fun pairSecond(variableName: String) {
        builderOps.add { linePart(pairSecond(variableName, this.c.lang)) }
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        builderOps.forEach { this.apply(it) }
    }
}
typealias BodyBuilderOps = BodyBuilder.() -> Unit
fun body(block: BodyBuilderOps) = BodyBuilder().apply(block)

class MethodBuilder: CodeBlockBuilder {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilder? = null
    var body: BodyBuilderOps? = null

    private val args: MutableList<ArgumentBuilder> = mutableListOf()
    fun addArg(block: ArgumentBuilderOps) {
        args.add(argument(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val overridePart = if (override) "override " else ""

        linePart("${overridePart}${c.lang.methodDeclarationKeyword()}$name(")
        args.forEachIndexed { index, arg ->
            add(arg)
            if (index != args.size - 1) {
                linePart(", ")
            }
        }
        linePart(")")
        returnType?.let {
            linePart(": ")
            add(it)
        }
        linePart(" {")

        tab()
        body?.let { add(body(it)) }
        untab()

        line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
