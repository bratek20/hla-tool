package com.github.bratek20.codebuilder

import com.github.bratek20.codebuilder.builders.ClassBuilder
import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.FunctionBuilder
import com.github.bratek20.codebuilder.builders.FunctionBuilderOps

class CodeBuilderContext(
    val lang: CodeBuilderLanguage
)
interface CodeBlockBuilder {
    fun getOperations(c: CodeBuilderContext): CodeBuilderOps
}

class CodeBuilder(
    lang: CodeBuilderLanguage,
    indent: Int = 0
) {
    val c: CodeBuilderContext = CodeBuilderContext(lang)
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
        this.apply(block.getOperations(c))
        return this
    }

    fun tab(): CodeBuilder {
        currentIndent += 4
        linePartStarted = false
        return this
    }

    fun untab(): CodeBuilder {
        currentIndent -= 4
        linePartStarted = false
        return this
    }

    fun addClass(block: ClassBuilderOps): CodeBuilder {
        val clazz = ClassBuilder().apply(block)
        return add(clazz)
    }

    fun addFunction(block: FunctionBuilderOps): CodeBuilder {
        val function = FunctionBuilder().apply(block)
        return add(function)
    }

    fun addFunctions(blocks: List<FunctionBuilderOps>): CodeBuilder {
        blocks.forEach { addFunction(it) }
        return this
    }

    fun build(): String {
        return lines.joinToString("\n")
    }
}
typealias CodeBuilderOps = CodeBuilder.() -> Unit