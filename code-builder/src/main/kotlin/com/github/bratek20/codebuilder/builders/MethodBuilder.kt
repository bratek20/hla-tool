package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

fun pairFirst(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairFirst(variableName)
}

fun pairSecond(variableName: String, lang: CodeBuilderLanguage): String {
    return lang.pairSecond(variableName)
}

class PairTypeBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    lateinit var first: TypeBuilderOps
    lateinit var second: TypeBuilderOps

    override fun applyOperations(b: CodeBuilder) {
        b.linePart(lang.pairTypeStart())
        b.add(TypeBuilder(lang).apply(first))
        b.linePart(", ")
        b.add(TypeBuilder(lang).apply(second))
        b.linePart(lang.pairTypeEnd())
    }
}
typealias PairTypeBuilderOps = PairTypeBuilder.() -> Unit

class TypeBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String? = null
    var base: BaseType? = null
    var pair: PairTypeBuilderOps? = null

    override fun applyOperations(b: CodeBuilder) {
        if (name != null) {
            b.linePart(name!!)
        } else if (base != null) {
            b.linePart(lang.mapBaseType(base!!))
        } else if (pair != null) {
            b.add(PairTypeBuilder(lang).apply(pair!!))
        } else {
            throw IllegalStateException("TypeBuilder must have one of the fields set")
        }
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

class BodyBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    private val builderOps = mutableListOf<CodeBuilderOps>()

    fun line(value: String) {
        builderOps.add { line(value) }
    }

    fun linePart(value: String) {
        builderOps.add { linePart(value) }
    }

    fun pairFirst(variableName: String) {
        builderOps.add { linePart(pairFirst(variableName, lang)) }
    }

    fun pairSecond(variableName: String) {
        builderOps.add { linePart(pairSecond(variableName, lang)) }
    }

    override fun applyOperations(b: CodeBuilder) {
        builderOps.forEach { b.apply(it) }
    }
}
typealias BodyBuilderOps = BodyBuilder.() -> Unit

class MethodBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    lateinit var name: String

    var override: Boolean = false
    var returnType: TypeBuilderOps? = null
    var body: BodyBuilderOps? = null

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
        body?.let { b.add(BodyBuilder(lang).apply(it)) }
        b.untab()

        b.line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
