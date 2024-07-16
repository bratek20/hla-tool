package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun returnBlock(block: CodeBuilderOps): CodeBuilderOps = {
    linePart("return ")
    add(block)
}

class AddOpArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun plus(block: AddOpArgs.()->Unit): CodeBuilderOps = {
    val args = AddOpArgs().apply(block)
    add(args.left)
    linePart(" + ")
    add(args.right)
}

fun asLinePart(value: String): CodeBuilderOps = {
    linePart(value)
}