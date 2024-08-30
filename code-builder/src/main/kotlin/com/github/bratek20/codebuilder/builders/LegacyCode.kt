package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

fun CodeBuilder.legacyConst(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.legacyVariable(name: String): CodeBuilder {
    return linePart(name)
}

fun CodeBuilder.legacyString(name: String): CodeBuilder {
    return linePart("\"$name\"")
}

fun CodeBuilder.legacyComment(value: String): CodeBuilder {
    return line("// $value")
}

class AssignArgs {
    lateinit var variable: VariableAssignmentBuilderOps
    lateinit var value: CodeBuilderOps
}
fun CodeBuilder.legacyAssign(block: AssignArgs.()->Unit): CodeBuilder {
    val args = AssignArgs().apply(block)
    add(VariableAssignmentBuilder().apply(args.variable))
    linePart(" = ")
    addOps(args.value)
    statementLineEnd()
    return this
}

class PlusArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun CodeBuilder.legacyPlus(block: PlusArgs.()->Unit): CodeBuilder {
    val args = PlusArgs().apply(block)
    addOps(args.left)
    linePart(" + ")
    addOps(args.right)
    return this
}

class LegacyIsEqualToArgs {
    lateinit var left: CodeBuilderOps
    lateinit var right: CodeBuilderOps
}
fun CodeBuilder.legacyIsEqualTo(block: LegacyIsEqualToArgs.()->Unit): CodeBuilder {
    val args = LegacyIsEqualToArgs().apply(block)
    addOps(args.left)
    linePart(" == ")
    addOps(args.right)
    return this
}

fun CodeBuilder.legacyReturn(block: CodeBuilderOps): CodeBuilder {
    lineStart("return ")
    addOps(block)
    statementLineEnd()
    return this
}