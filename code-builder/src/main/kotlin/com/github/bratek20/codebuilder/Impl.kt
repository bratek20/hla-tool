package com.github.bratek20.codebuilder


interface CodeBlockBuilder {
    fun apply(b: CodeBuilder)
}

interface CodeBuilderLanguage {
    fun implements(): String
}

class Kotlin: CodeBuilderLanguage {
    override fun implements(): String {
        return ": "
    }
}

class TypeScript: CodeBuilderLanguage {
    override fun implements(): String {
        return " implements "
    }
}

abstract class BaseCodeBlockBuilder: CodeBlockBuilder {
    protected lateinit var lang: CodeBuilderLanguage

    fun init(lang: CodeBuilderLanguage) {
        this.lang = lang
    }
}

class EmptyBlock: CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        // do nothing
    }
}

class OneLineBlock(
    private val block: String
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        b.line(block)
    }
}

class EmptyLineBlock: CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        b.emptyLine()
    }
}

class ManyCodeBlocksSeparatedByLine(
    private val blocks: List<CodeBlockBuilder>
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        blocks.dropLast(1).forEach {
            it.apply(b)
            b.emptyLine()
        }
        blocks.last().apply(b)
    }
}

fun block(block: CodeBuilder.() -> Unit): CodeBlockBuilder {
    return object : CodeBlockBuilder {
        override fun apply(b: CodeBuilder) {
            b.block()
        }
    }
}

class ListFieldDeclaration(
    private val fieldName: String,
    private val fieldElementType: String
): BaseCodeBlockBuilder() {
    override fun apply(b: CodeBuilder) {
        if (lang is TypeScript)
            b.line("private readonly $fieldName: $fieldElementType[] = []")
        else
            b.line("private val $fieldName = mutableListOf<$fieldElementType>()")
    }
}

class Class(
    private val className: String,
    private val implementedInterfaceName: String,
    private val body: CodeBlockBuilder
): BaseCodeBlockBuilder() {
    override fun apply(b: CodeBuilder) {
        b.line("class $className${lang.implements()}$implementedInterfaceName {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}

class Function(
    private val override: Boolean = false,
    private val name: String,
    private val returnType: String? = null,
    private val args: List<Pair<String, String>>,
    private val body: CodeBlockBuilder
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        val overridePart = if (override) "override " else ""
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        b.line("${overridePart}fun $name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}

class CodeBuilder(
    private val lang: CodeBuilderLanguage,
    indent: Int = 0
) {
    private var currentIndent = indent
    val lines = mutableListOf<String>()

    fun line(value: String): CodeBuilder {
        val indent = " ".repeat(currentIndent)
        lines.add(indent + value)
        return this
    }

    fun add(block: CodeBlockBuilder): CodeBuilder {
        if (block is BaseCodeBlockBuilder) {
            block.init(lang)
        }
        block.apply(this)
        return this
    }

    fun tab(): CodeBuilder {
        currentIndent += 4
        return this
    }

    fun emptyLine(): CodeBuilder {
        lines.add("")
        return this
    }

    fun untab(): CodeBuilder {
        currentIndent -= 4
        return this
    }

    fun build(): String {
        return lines.joinToString("\n")
    }
}