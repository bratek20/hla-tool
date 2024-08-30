package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

interface ExpressionBuilder: CodeBlockBuilder

class ReturnBuilder(
    private val value: ExpressionBuilder
): CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart("return ")
        add(value)
        statementLineEnd()
    }
}

class ExpressionLinePartBuilder(
    val value: String,
): ExpressionBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(value)
    }
}
fun expression(value: String): ExpressionBuilder {
    return ExpressionLinePartBuilder(value)
}

fun variable(name: String): ExpressionBuilder {
    return expression(name)
}

fun const(value: Int): ExpressionBuilder {
    return expression(value.toString())
}

fun string(value: String): ExpressionBuilder {
    return expression("\"$value\"")
}

class VariableAssignmentBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var value: ExpressionBuilder

    var declare: Boolean = false
    var mutable: Boolean = false

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart()

        if (declare) {
            if (mutable) {
                linePart(c.lang.mutableVariableDeclaration())
            } else {
                linePart(c.lang.immutableVariableDeclaration())
            }
        }

        linePart("$name = ")
        add(value)

        statementLineEnd()
    }
}
typealias VariableAssignmentBuilderOps = VariableAssignmentBuilder.() -> Unit
fun variableAssignment(block: VariableAssignmentBuilderOps) = VariableAssignmentBuilder().apply(block)

class PlusBuilder: ExpressionBuilder {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart()
        add(left)
        linePart(" + ")
        add(right)
    }
}
typealias PlusBuilderOps = PlusBuilder.() -> Unit
fun plus(ops: PlusBuilderOps): PlusBuilder {
    return PlusBuilder().apply(ops)
}