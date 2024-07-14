package com.github.bratek20.codebuilder

import com.github.bratek20.codebuilder.clazz.ClassBuilder

interface CodeBlockBuilder {
    fun apply(b: CodeBuilder)
}


abstract class BaseCodeBlockBuilder: CodeBlockBuilder {
    protected lateinit var lang: CodeBuilderLanguage

    fun init(lang: CodeBuilderLanguage) {
        this.lang = lang
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

    fun addClass(block: ClassBuilder.() -> Unit): CodeBuilder {
        val clazz = ClassBuilder().apply(block)
        return add(clazz)
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