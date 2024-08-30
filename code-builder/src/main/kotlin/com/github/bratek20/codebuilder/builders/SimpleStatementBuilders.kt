package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

//full line
interface StatementBuilder: CodeBlockBuilder
typealias StatementBuilderProvider = () -> StatementBuilder

class ReturnBuilder(
    private val value: ExpressionBuilder
): StatementBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart("return ")
        add(value)
        statementLineEnd()
    }
}
fun returnStatement(value: ExpressionBuilderProvider): ReturnBuilder {
    return ReturnBuilder(value.invoke())
}

class VariableAssignmentBuilder: StatementBuilder {
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
