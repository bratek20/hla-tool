package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.LinePartBuilder

fun CodeBuilder.returnBlock(block: CodeBuilderOps): CodeBuilder {
    linePart("return ")
    add(block)
    return this
}

class AddOpArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun CodeBuilder.plus(block: AddOpArgs.()->Unit): CodeBuilder {
    val args = AddOpArgs().apply(block)
    add(args.left)
    linePart(" + ")
    add(args.right)
    return this
}

fun CodeBuilder.variable(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.string(name: String): CodeBuilder {
    return linePart("\"$name\"")
}