package com.github.bratek20.codebuilder

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