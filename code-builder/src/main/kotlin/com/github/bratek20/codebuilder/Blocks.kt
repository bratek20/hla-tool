package com.github.bratek20.codebuilder

class EmptyBlock: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        // do nothing
    }
}

class OneLineBlock(
    private val block: String
): CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line(block)
    }
}

class EmptyLineBlock: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        emptyLine()
    }
}

class ManyCodeBlocksSeparatedByLine(
    private val blocks: List<CodeBlockBuilder>
): CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        blocks.dropLast(1).forEach {
            add(it)
            emptyLine()
        }
        add(blocks.last())
    }
}

fun block(block: CodeBuilder.() -> Unit): CodeBlockBuilder {
    return object : CodeBlockBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            block()
        }
    }
}