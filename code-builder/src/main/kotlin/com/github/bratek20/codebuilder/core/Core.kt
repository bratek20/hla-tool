package com.github.bratek20.codebuilder.core

class CodeBuilderContext(
    val lang: CodeBuilderLanguage
)

data class ValidationResult(
    val errorMessage: String?
) {
    companion object {
        fun success() = ValidationResult(null)
        fun failure(message: String) = ValidationResult(message)
    }
}

class CodeBuilderValidationException(message: String): CodeBuilderException(message)

interface CodeBlockBuilder {
    fun getOperations(c: CodeBuilderContext): CodeBuilderOps

    fun validate(c: CodeBuilderContext): ValidationResult = ValidationResult.success()
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
fun noOpBlock() = object: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {}
}

interface LinePartBuilder {
    fun build(c: CodeBuilderContext): String
}
fun linePartBlock(value: String) = object: LinePartBuilder {
    override fun build(c: CodeBuilderContext): String = value
}

open class CodeBuilderException(message: String): Exception(message)

class CodeBuilder(
    lang: CodeBuilderLanguage,
    indent: Int = 0
) {
    val c: CodeBuilderContext = CodeBuilderContext(lang)
    private val lines = mutableListOf<String>()

    private var currentIndent = indent
    private var lineManipulationStarted = false
    private var lineManipulationSoftEnd = false

    fun line(value: String): CodeBuilder {
        return addFullLine("${indentString()}$value")
    }

    fun emptyLine(): CodeBuilder {
        return addFullLine("")
    }

    private fun addFullLine(value: String): CodeBuilder {
        lines.add(value)
        onLineEnd()
        return this
    }

    private fun indentString(): String {
        return " ".repeat(currentIndent)
    }

    fun lineStart(value: String? = null): CodeBuilder {
        if (lineManipulationStarted && !lineManipulationSoftEnd) {
            val msg = "lineStart(\"${value ?: ""}\") failed - line already started, current value: \"${previousLine()}\"! Previous full line: \"${previousPreviousLine()}\""
            throw CodeBuilderException(msg)
        }

        startLineManipulation()

        value?.let { linePart(it) }

        return this
    }

    private fun startLineManipulation() {
        lineManipulationStarted = true
        lineManipulationSoftEnd = false
        lines.add(indentString())
    }

    fun lineSoftStart(value: String? = null): CodeBuilder {
        if (!lineManipulationStarted || lineManipulationSoftEnd) {
            startLineManipulation()
        }

        value?.let { linePart(it) }
        return this
    }

    private fun previousPreviousLine(): String {
        return if (lines.size < 2) {
            ""
        } else {
            lines[lines.size - 2]
        }
    }

    private fun previousLine(): String {
        return if (lines.isEmpty()) {
            ""
        } else {
            lines[lines.size - 1]
        }
    }

    fun linePart(value: String): CodeBuilder {
        throwIfLineNotStarted("linePart", value)

        lines[lines.size - 1] += value
        return this
    }

    fun lineEnd(value: String? = null): CodeBuilder {
        throwIfLineNotStarted("lineEnd", value ?: "")

        linePart(value ?: "")
        lineManipulationStarted = false
        return this
    }

    fun statementLineEnd(): CodeBuilder {
        return lineEnd(c.lang.statementTerminator())
    }

    fun lineSoftEnd(value: String? = null): CodeBuilder {
        throwIfLineNotStarted("lineSoftEnd", value ?: "")

        lineManipulationSoftEnd = true
        linePart(value ?: "")
        return this
    }

    private fun throwIfLineNotStarted(methodName: String, value: String) {
        if (!lineManipulationStarted) {
            val msg = "$methodName(\"$value\") failed - line not started! Previous line: ${previousLine()}"
            throw CodeBuilderException(msg)
        }
    }

    fun add(block: CodeBlockBuilder): CodeBuilder {
        block.validate(c).let {
            if (it.errorMessage != null) {
                throw CodeBuilderValidationException(it.errorMessage)
            }
        }
        this.apply(block.getOperations(c))
        return this
    }

    fun addOps(ops: CodeBuilderOps): CodeBuilder {
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

    fun addManyWithEmptyLineBetween(builders: List<CodeBlockBuilder>): CodeBuilder {
        builders.forEachIndexed { index, builder ->
            add(builder)
            if (index < builders.size - 1) {
                emptyLine()
            }
        }
        return this
    }

    fun tab(): CodeBuilder {
        currentIndent += 4
        onLineEnd()
        return this
    }

    fun untab(): CodeBuilder {
        currentIndent -= 4
        onLineEnd()
        return this
    }

    private fun onLineEnd() {
        lineManipulationStarted = false
        lineManipulationSoftEnd = false
    }


    fun build(): String {
        return lines.joinToString("\n")
    }
}
typealias CodeBuilderOps = CodeBuilder.() -> Unit