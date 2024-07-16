package com.github.bratek20.codebuilder.core

class CodeBuilderContext(
    val lang: CodeBuilderLanguage
)

interface CodeBlockBuilder {
    fun getOperations(c: CodeBuilderContext): CodeBuilderOps
}
fun lineBlock(value: String) = object: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line(value)
    }
}

interface LinePartBuilder {
    fun build(c: CodeBuilderContext): String
}
fun linePartBlock(value: String) = object: LinePartBuilder {
    override fun build(c: CodeBuilderContext): String = value
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

    fun endLinePart(): CodeBuilder {
        linePartStarted = false
        return this
    }

    fun add(block: CodeBlockBuilder): CodeBuilder {
        this.apply(block.getOperations(c))
        return this
    }

    fun add(ops: CodeBuilderOps): CodeBuilder {
        ops(this)
        return this
    }

    fun add(linePartBuilder: LinePartBuilder): CodeBuilder {
        return linePart(linePartBuilder.build(c))
    }

    fun addMany(builders: List<CodeBlockBuilder>): CodeBuilder {
        builders.forEach { add(it) }
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


    fun build(): String {
        return lines.joinToString("\n")
    }
}
typealias CodeBuilderOps = CodeBuilder.() -> Unit