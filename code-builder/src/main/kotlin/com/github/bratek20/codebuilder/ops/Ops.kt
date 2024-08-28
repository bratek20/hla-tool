package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.*

fun CodeBuilder.returnBlock(block: CodeBuilderOps): CodeBuilder {
    lineStart("return ")
    add(block)
    statementLineEnd()
    return this
}

class VariableAssignBuilder: CodeBlockBuilder {
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
typealias VariableAssignBuilderOps = VariableAssignBuilder.() -> Unit

class AssignArgs {
    lateinit var variable: VariableAssignBuilderOps
    lateinit var value: CodeBuilderOps
}
fun CodeBuilder.assign(block: AssignArgs.()->Unit): CodeBuilder {
    val args = AssignArgs().apply(block)
    add(VariableAssignBuilder().apply(args.variable))
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

fun CodeBuilder.variableLegacy(name: String): CodeBuilder {
    return linePart(name)
}

class VariableBuilder(
    val name: String,
): LinePartBuilder {
    override fun build(c: CodeBuilderContext): String {
        return name
    }
}
fun variable(name: String): VariableBuilder {
    return VariableBuilder(name)
}

fun CodeBuilder.string(name: String): CodeBuilder {
    return linePart("\"$name\"")
}

fun CodeBuilder.comment(value: String): CodeBuilder {
    return line("// $value")
}