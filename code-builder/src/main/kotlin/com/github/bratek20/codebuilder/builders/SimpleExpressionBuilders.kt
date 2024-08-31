package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.utils.camelToPascalCase

//part of line
interface ExpressionBuilder: CodeBlockBuilder {
    fun getValue(c: CodeBuilderContext): String {
        TODO("not implemented")
    }
}
typealias ExpressionBuilderProvider = () -> ExpressionBuilder

fun expression(value: String) = object : ExpressionBuilder {
    override fun getValue(c: CodeBuilderContext): String {
        return value
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(value)
    }
}
fun expression(valueProvider: (CodeBuilderContext) -> String) = object : ExpressionBuilder {
    override fun getValue(c: CodeBuilderContext): String {
        return valueProvider(c)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart(valueProvider(c))
    }
}

fun variable(name: String): ExpressionBuilder {
    return expression(name)
}

class GetterFieldAccessBuilder: ExpressionBuilder {
    lateinit var variableName: String
    lateinit var fieldName: String

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val finalFieldName = if (c.lang.areMethodsPascalCase()) {
            camelToPascalCase(fieldName)
        } else {
            fieldName
        }
        linePart("$variableName.$finalFieldName")
    }
}
typealias GetterFieldAccessBuilderOps = GetterFieldAccessBuilder.() -> Unit
fun getterFieldAccess(ops: GetterFieldAccessBuilderOps) = GetterFieldAccessBuilder().apply(ops)

fun instanceVariable(name: String): ExpressionBuilder = object : ExpressionBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart("${c.lang.softThis()}$name")
    }
}

fun const(value: Int): ExpressionBuilder {
    return expression(value.toString())
}

fun nullValue() = expression { c -> c.lang.nullValue() }

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

class IsEqualToArgs {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder
}
fun isEqualTo(block: IsEqualToArgs.() -> Unit) = object : ExpressionBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val args = IsEqualToArgs().apply(block)

        add(args.left)
        linePart(" == ")
        add(args.right)
    }
}