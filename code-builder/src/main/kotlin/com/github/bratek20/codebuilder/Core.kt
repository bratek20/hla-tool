package com.github.bratek20.codebuilder

import com.github.bratek20.codebuilder.builders.ClassBuilder
import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.FunctionBuilder
import com.github.bratek20.codebuilder.builders.FunctionBuilderOps

interface CodeBlockBuilder {
    fun applyOperations(b: CodeBuilder)
}

abstract class LangCodeBlockBuilder(
    protected val lang: CodeBuilderLanguage
): CodeBlockBuilder

class CodeBuilder(
    val lang: CodeBuilderLanguage,
    indent: Int = 0
) {
    private val lines = mutableListOf<String>()

    private var currentIndent = indent
    private var linePartStarted = false

    fun line(value: String): CodeBuilder {
        return addFullLine("${indentString()}$value")
    }

    fun emptyLine(): CodeBuilder {
        return addFullLine("")
    }

    private fun addFullLine(value: String): CodeBuilder {
        lines.add(value)
        linePartStarted = false
        return this
    }

    private fun indentString(): String {
        return " ".repeat(currentIndent)
    }

    fun linePart(value: String): CodeBuilder {
        if (!linePartStarted) {
            linePartStarted = true
            lines.add(indentString())
        }
        lines[lines.size - 1] += value
        return this
    }

    fun add(block: CodeBlockBuilder): CodeBuilder {
        block.applyOperations(this)
        return this
    }

    fun tab(): CodeBuilder {
        currentIndent += 4
        return this
    }

    fun untab(): CodeBuilder {
        currentIndent -= 4
        return this
    }

    fun addClass(block: ClassBuilderOps): CodeBuilder {
        val clazz = ClassBuilder(lang).apply(block)
        return add(clazz)
    }

    fun addFunction(block: FunctionBuilderOps): CodeBuilder {
        val function = FunctionBuilder(lang).apply(block)
        return add(function)
    }

    fun build(): String {
        return lines.joinToString("\n")
    }
}