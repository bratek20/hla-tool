package com.github.bratek20.codebuilder

class EmptyBlock: CodeBlockBuilder {
    override fun applyOperations(b: CodeBuilder) {
        // do nothing
    }
}

class OneLineBlock(
    private val block: String
): CodeBlockBuilder {
    override fun applyOperations(b: CodeBuilder) {
        b.line(block)
    }
}

class EmptyLineBlock: CodeBlockBuilder {
    override fun applyOperations(b: CodeBuilder) {
        b.emptyLine()
    }
}

class ManyCodeBlocksSeparatedByLine(
    private val blocks: List<CodeBlockBuilder>
): CodeBlockBuilder {
    override fun applyOperations(b: CodeBuilder) {
        blocks.dropLast(1).forEach {
            b.add(it)
            b.emptyLine()
        }
        b.add(blocks.last())
    }
}

fun block(block: CodeBuilder.() -> Unit): CodeBlockBuilder {
    return object : CodeBlockBuilder {
        override fun applyOperations(b: CodeBuilder) {
            b.block()
        }
    }
}