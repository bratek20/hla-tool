package com.github.bratek20.hla.codebuilder


interface CodeLineBuilder {
    fun build(): String
}

interface CodeBlockBuilder {
    fun apply(b: CodeBuilder)
}

class OneLineBlock(
    private val block: String
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        b.line(block)
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
): CodeLineBuilder {
    override fun build(): String {
        return "private val $fieldName = mutableListOf<$fieldElementType>()"
    }
}

class Class(
    private val className: String,
    private val implementedInterfaceName: String,
    private val body: CodeBlockBuilder
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        b.line("class $className: $implementedInterfaceName {")
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
    indent: Int = 0
) {
    private var currentIndent = indent
    val lines = mutableListOf<String>()

    fun line(value: String): CodeBuilder {
        val indent = " ".repeat(currentIndent)
        lines.add(indent + value)
        return this
    }

    fun line(value: CodeLineBuilder): CodeBuilder {
        return line(value.build())
    }

    fun add(block: CodeBlockBuilder): CodeBuilder {
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