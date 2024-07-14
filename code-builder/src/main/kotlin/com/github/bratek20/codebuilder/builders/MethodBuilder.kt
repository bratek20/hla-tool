package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class TypeBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String? = null
    var base: BaseType? = null

    override fun applyOperations(b: CodeBuilder) {
        val finalName = name ?: lang.mapBaseType(base!!)
        b.linePart(finalName)
    }
}
typealias TypeBuilderOps = TypeBuilder.() -> Unit

class ArgumentBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    lateinit var name: String
    lateinit var type: TypeBuilderOps

    override fun applyOperations(b: CodeBuilder) {
        b.linePart("$name: ")
        b.add(TypeBuilder(lang).apply(type))
    }
}
typealias ArgumentBuilderOps = ArgumentBuilder.() -> Unit

class MethodBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilderOps? = null
    var body: CodeBlockBuilder = EmptyBlock()

    private val args: MutableList<ArgumentBuilder> = mutableListOf()
    fun addArg(block: ArgumentBuilderOps) {
        args.add(ArgumentBuilder(lang).apply(block))
    }

    override fun applyOperations(b: CodeBuilder) {
        val overridePart = if (override) "override " else ""

        b.linePart("${overridePart}${lang.methodDeclarationKeyword()}$name(")
        args.forEachIndexed { index, arg ->
            b.add(arg)
            if (index != args.size - 1) {
                b.linePart(", ")
            }
        }
        b.linePart(")")
        returnType?.let {
            b.linePart(": ")
            b.add(TypeBuilder(lang).apply(it))
        }
        b.linePart(" {")

        b.tab()
        body.applyOperations(b)
        b.untab()

        b.line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
