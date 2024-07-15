package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

fun pairFirst(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairFirst(variableName)
}

fun pairSecond(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairSecond(variableName)
}

class PairTypeBuilder: CodeBlockBuilder {
    lateinit var first: TypeBuilderOps
    lateinit var second: TypeBuilderOps

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(c.lang.pairTypeStart())
        add(type(first))
        linePart(", ")
        add(type(second))
        linePart(c.lang.pairTypeEnd())
    }
}
typealias PairTypeBuilderOps = PairTypeBuilder.() -> Unit

class TypeBuilder: CodeBlockBuilder {
    var name: String? = null
    var base: BaseType? = null
    var pair: PairTypeBuilderOps? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        if (name != null) {
            linePart(name!!)
        } else if (base != null) {
            linePart(c.lang.mapBaseType(base!!))
        } else if (pair != null) {
            add(PairTypeBuilder().apply(pair!!))
        } else {
            throw IllegalStateException("TypeBuilder must have one of the fields set")
        }
    }
}
typealias TypeBuilderOps = TypeBuilder.() -> Unit
fun type(block: TypeBuilderOps) = TypeBuilder().apply(block)

class ArgumentBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilderOps

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        return {
            linePart("$name: ")
            add(TypeBuilder().apply(type))
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
    var returnType: TypeBuilderOps? = null
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
            add(type(it))
        }
        linePart(" {")

        tab()
        body?.let { add(body(it)) }
        untab()

        line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
