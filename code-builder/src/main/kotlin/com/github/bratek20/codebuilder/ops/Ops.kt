package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun CodeBuilder.returnBlock(block: CodeBuilderOps): CodeBuilder {
    lineStart("return ")
    add(block)
    statementLineEnd()
    return this
}

class VariableBuilder: CodeBlockBuilder {
    lateinit var name: String
    var declare: Boolean = false
    var mutable: Boolean = false

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart()
        if (declare) {
            if (mutable) {
                linePart(c.lang.mutableVariableDeclaration())
            } else {
                linePart(c.lang.immutableVariableDeclaration())
            }
        }
        linePart(name)
    }
}
typealias VariableBuilderOps = VariableBuilder.() -> Unit

class AssignArgs {
    lateinit var variable: VariableBuilderOps
    lateinit var value: CodeBuilderOps
}
fun CodeBuilder.assign(block: AssignArgs.()->Unit): CodeBuilder {
    val args = AssignArgs().apply(block)
    add(VariableBuilder().apply(args.variable))
    linePart(" = ")
    add(args.value)
    statementLineEnd()
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