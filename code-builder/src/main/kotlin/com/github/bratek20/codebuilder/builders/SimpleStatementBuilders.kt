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

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        if (type == null || c.lang.typeDeclarationStyle() == TypeDeclarationStyle.VARIABLE_FIRST) {
            if (mutable) {
                linePart(c.lang.mutableVariableDeclaration())
            } else {
                linePart(c.lang.immutableVariableDeclaration())
            }
        }

        when(c.lang.typeDeclarationStyle()) {
            TypeDeclarationStyle.TYPE_FIRST -> {
                type?.let {
                    add(it)
                    linePart(" ")
                }
                linePart(name)
            }
            TypeDeclarationStyle.VARIABLE_FIRST -> {
                linePart(name)
                type?.let {
                    linePart(": ")
                    add(it)
                }
            }
        }
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
