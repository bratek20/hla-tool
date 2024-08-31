package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.TypeDeclarationStyle
import com.github.bratek20.codebuilder.types.TypeBuilder

//full line
interface StatementBuilder: CodeBlockBuilder
typealias StatementBuilderProvider = () -> StatementBuilder

fun statement(value: String) = object : StatementBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart(value)
        statementLineEnd()
    }
}
fun statement(block: (CodeBuilderContext) -> CodeBuilderOps) = object : StatementBuilder {
    override fun getOperations(c: CodeBuilderContext) = block(c)
}

fun expressionToStatement(value: ExpressionBuilderProvider) = statement {{
    lineStart()
    add(value.invoke())
    statementLineEnd()
}}

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
class VariableDeclarationBuilder: ExpressionBuilder {
    lateinit var name: String
    var mutable: Boolean = false
    var type: TypeBuilder? = null

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        if (type == null || c.lang.typeDeclarationStyle() == TypeDeclarationStyle.VARIABLE_FIRST) {
            if (mutable) {
                b.append(c.lang.mutableVariableDeclaration())
            } else {
                b.append(c.lang.immutableVariableDeclaration())
            }
        }

        when(c.lang.typeDeclarationStyle()) {
            TypeDeclarationStyle.TYPE_FIRST -> {
                type?.let {
                    b.append(it.build(c))
                    b.append(" ")
                }
                b.append(name)
            }
            TypeDeclarationStyle.VARIABLE_FIRST -> {
                b.append(name)
                type?.let {
                    b.append(": ")
                    b.append(it.build(c))
                }
            }
        }
        return b.toString()
    }
}
fun variableDeclaration(block: VariableDeclarationBuilder.() -> Unit) = VariableDeclarationBuilder().apply(block)

class AssignmentBuilder: StatementBuilder {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineStart()
        add(left)
        linePart(" = ")
        add(right)
        statementLineEnd()
    }
}
typealias AssignmentBuilderOps = AssignmentBuilder.() -> Unit
fun assignment(block: AssignmentBuilderOps) = AssignmentBuilder().apply(block)
