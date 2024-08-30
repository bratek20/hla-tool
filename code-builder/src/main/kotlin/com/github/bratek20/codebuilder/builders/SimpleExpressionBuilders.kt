package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

//part of line
interface ExpressionBuilder: CodeBlockBuilder
typealias ExpressionBuilderProvider = () -> ExpressionBuilder

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


class CommentBuilder(
    private val comment: String,
): ExpressionBuilder, StatementBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart("// $comment")
        lineSoftEnd()
    }
}
typealias StringProvider = () -> String
fun comment(comment: StringProvider): CommentBuilder {
    return CommentBuilder(comment())
}