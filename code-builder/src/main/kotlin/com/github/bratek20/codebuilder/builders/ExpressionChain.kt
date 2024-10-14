package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class ExpressionChainBuilder(
    firstExpression: ExpressionBuilder
): ExpressionBuilder {
    private val ops: MutableList<ExpressionBuilder> = mutableListOf()

    init {
        ops.add(firstExpression)
    }

    fun then(block: ExpressionBuilderProvider): ExpressionChainBuilder {
        ops.add(block())
        return this
    }

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        ops.forEachIndexed { index, expression ->
            b.append(expression.build(c))
            if (index < ops.size - 1) {
                b.append(".")
            }
        }
        return b.toString()
    }

    override fun asStatement(): ExpressionChainStatementBuilder {
        return ExpressionChainStatementBuilder(this)
    }
}

fun expressionChain(e: ExpressionBuilderProvider) = ExpressionChainBuilder(e.invoke())
class ExpressionChainStatementBuilder: StatementBuilder {
    private val expressions: MutableList<ExpressionBuilder> = mutableListOf()
    constructor(firstExpression: ExpressionBuilder) {
        expressions.add(firstExpression)
    }

    fun then(block: ExpressionBuilderProvider): ExpressionChainStatementBuilder {
        expressions.add(block())
        return this
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        if (expressions.size == 1) {
            return {
                lineStart(expressions.first().build(c))
                statementLineEnd()
            }
        }

        return {
            line(expressions.first().build(c))
            tab()
            expressions.drop(1).forEachIndexed { idx, expression  ->
                lineStart(".")
                linePart(expression.build(c))

                if (idx == expressions.size - 2) {
                    statementLineEnd()
                }
                else {
                    lineEnd()
                }
            }
            untab()
        }
    }
}
fun expressionChainStatement(e: ExpressionBuilderProvider) = ExpressionChainStatementBuilder(e.invoke())