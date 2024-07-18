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
fun emptyLineBlock() = object: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        emptyLine()
    }
}

interface LinePartBuilder {
    fun build(c: CodeBuilderContext): String
}
fun linePartBlock(value: String) = object: LinePartBuilder {
    override fun build(c: CodeBuilderContext): String = value
}

class CodeBuilderException(message: String): Exception(message)

class CodeBuilder(
    lang: CodeBuilderLanguage,
    indent: Int = 0
) {
    val c: CodeBuilderContext = CodeBuilderContext(lang)
    private val lines = mutableListOf<String>()

    private var currentIndent = indent
    private var linePartModification = false

    fun line(value: String): CodeBuilder {
        return addFullLine("${indentString()}$value")
    }

    fun emptyLine(): CodeBuilder {
        return addFullLine("")
    }

    private fun addFullLine(value: String): CodeBuilder {
        lines.add(value)
        linePartModification = false
        return this
    }

    private fun indentString(): String {
        return " ".repeat(currentIndent)
    }

    fun lineStart(value: String? = null): CodeBuilder {
        if (linePartModification) {
            throw CodeBuilderException("lineStart() already called")
        }

        linePartModification = true
        lines.add(indentString())

        value?.let { linePart(it) }

        return this
    }

    fun linePart(value: String): CodeBuilder {
        throwIfLineNotStarted("linePart()")

        lines[lines.size - 1] += value
        return this
    }

    fun lineEnd(value: String? = null): CodeBuilder {
        throwIfLineNotStarted("lineEnd()")

        value?.let { linePart(it) }
        linePartModification = false
        return this
    }

    private fun throwIfLineNotStarted(name: String) {
        if (!linePartModification) {
            throw CodeBuilderException("$name without lineStart() is not allowed")
        }
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
        linePartModification = false
        return this
    }

    fun untab(): CodeBuilder {
        currentIndent -= 4
        linePartModification = false
        return this
    }


    fun build(): String {
        return lines.joinToString("\n")
    }
}
typealias CodeBuilderOps = CodeBuilder.() -> Unit