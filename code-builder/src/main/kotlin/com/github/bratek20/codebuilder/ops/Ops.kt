package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun CodeBuilder.returnBlock(block: CodeBuilderOps): CodeBuilder {
    lineStart("return ")
    add(block)
    lineEnd()
    return this
}

class AssignArgs {
    lateinit var variable: String
    lateinit var value: CodeBuilderOps
}
fun CodeBuilder.assign(block: AssignArgs.()->Unit): CodeBuilder {
    val args = AssignArgs().apply(block)
    lineStart("${args.variable} = ")
    add(args.value)
    lineEnd()
    return this
}

class PlusArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun CodeBuilder.plus(block: PlusArgs.()->Unit): CodeBuilder {
    val args = PlusArgs().apply(block)
    add(args.left)
    linePart(" + ")
    add(args.right)
    return this
}

class IsEqualToArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun CodeBuilder.isEqualTo(block: IsEqualToArgs.()->Unit): CodeBuilder {
    val args = IsEqualToArgs().apply(block)
    add(args.left)
    linePart(" == ")
    add(args.right)
    return this
}

fun CodeBuilder.const(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.variable(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.string(name: String): CodeBuilder {
    return linePart("\"$name\"")
}

fun CodeBuilder.comment(value: String): CodeBuilder {
    return line("// $value")
}