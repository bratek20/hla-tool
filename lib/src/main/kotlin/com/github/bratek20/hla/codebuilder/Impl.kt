package com.github.bratek20.hla.codebuilder


interface CodeLineBuilder {
    fun build(): String
}

class ListFieldDeclaration(
    private val fieldName: String,
    private val fieldElementType: String
): CodeLineBuilder {
    override fun build(): String {
        return "private val $fieldName = mutableListOf<$fieldElementType>()"
    }
}

class ClassDeclaration(
    private val className: String,
    private val implementedInterfaceName: String
): CodeLineBuilder {
    override fun build(): String {
        return "class $className: $implementedInterfaceName {"
    }

}
class CodeBuilder(
    indent: Int = 0
) {
    private var currentIndent = indent
    private val lines = mutableListOf<String>()

    fun line(value: String): CodeBuilder {
        val indent = " ".repeat(currentIndent)
        lines.add(indent + value)
        return this
    }

    fun line(value: CodeLineBuilder): CodeBuilder {
        return line(value.build())
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