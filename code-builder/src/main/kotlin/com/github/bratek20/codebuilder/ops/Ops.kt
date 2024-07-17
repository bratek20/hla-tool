package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.LinePartBuilder

fun returnBlock(block: CodeBuilderOps): CodeBuilderOps = {
    linePart("return ")
    add(block)
}

class AddOpArgs {
    lateinit var left: LinePartBuilder
    lateinit var right: LinePartBuilder
}
fun plus(block: AddOpArgs.()->Unit): CodeBuilderOps = {
    val args = AddOpArgs().apply(block)
    add(args.left)
    linePart(" + ")
    add(args.right)
}

fun variable(name: String): LinePartBuilder = object: LinePartBuilder {
    override fun build(c: CodeBuilderContext): String = name
}