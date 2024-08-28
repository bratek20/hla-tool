package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.*

fun CodeBuilder.returnBlock(block: CodeBuilderOps): CodeBuilder {
    lineStart("return ")
    add(block)
    statementLineEnd()
    return this
}

interface ExpressionBuilder: LinePartBuilder

class VariableBuilder(
    val name: String,
): ExpressionBuilder {
    override fun build(c: CodeBuilderContext): String {
        return name
    }
}
fun variable(name: String): VariableBuilder {
    return VariableBuilder(name)
}

class VariableAssignmentBuilder: CodeBlockBuilder {
    lateinit var name: String

    var declare: Boolean = false
    var mutable: Boolean = false


    var value: ExpressionBuilder? = null
    var blockValue: CodeBlockBuilder? = null

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

        value?.let {
            linePart(" = ")
            add(it)
            statementLineEnd()
        }
        blockValue?.let {
            linePart(" = ")
            add(it)
        }
    }
}
typealias VariableAssignmentBuilderOps = VariableAssignmentBuilder.() -> Unit
fun variableAssignment(block: VariableAssignmentBuilderOps) = VariableAssignmentBuilder().apply(block)

class AssignArgs {
    lateinit var variable: VariableAssignmentBuilderOps
    lateinit var value: CodeBuilderOps
}
fun CodeBuilder.assign(block: AssignArgs.()->Unit): CodeBuilder {
    val args = AssignArgs().apply(block)
    add(VariableAssignmentBuilder().apply(args.variable))
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

fun CodeBuilder.legacyConst(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.legacyVariable(name: String): CodeBuilder {
    return linePart(name)
}



fun CodeBuilder.string(name: String): CodeBuilder {
    return linePart("\"$name\"")
}

fun CodeBuilder.comment(value: String): CodeBuilder {
    return line("// $value")
}