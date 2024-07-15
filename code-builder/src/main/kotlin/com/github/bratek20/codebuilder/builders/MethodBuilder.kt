package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*
import com.github.bratek20.codebuilder.types.TypeBuilder

fun pairFirst(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairFirst(variableName)
}

fun pairSecond(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairSecond(variableName)
}








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

class MethodBuilder: CodeBlockBuilder {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilder? = null
    var body: CodeBuilderOps? = null

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
        body?.let { add(it) }
        untab()

        line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
