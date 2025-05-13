package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.utils.camelToPascalCase

//part of line
interface ExpressionBuilder: LinePartBuilder {
    fun asStatement(): StatementBuilder = expressionToStatement(this)
}

typealias ExpressionBuilderProvider = () -> ExpressionBuilder
fun expression(value: String) = object : ExpressionBuilder {
    override fun build(c: CodeBuilderContext): String {
        return value
    }
}
fun expression(valueProvider: (CodeBuilderContext) -> String) = object : ExpressionBuilder {
    override fun build(c: CodeBuilderContext): String {
        return valueProvider(c)
    }
}

fun variable(name: String): ExpressionBuilder {
    return expression(name)
}

fun parent(): ExpressionBuilder {
    return expression("base")
}

@Deprecated("if you are lazy, in the end all usages should be refactored to proper builders")
fun hardcodedExpression(value: String) = expression(value)

fun getterField(name: String) = expression { c ->
    if (c.lang.areMethodsPascalCase()) {
        camelToPascalCase(name)
    } else {
        name
    }
}

class GetterFieldAccessBuilder: ExpressionBuilder {
    lateinit var objectRef: ExpressionBuilder
    lateinit var fieldName: String

    override fun build(c: CodeBuilderContext): String {
        val finalFieldName = if (c.lang.areMethodsPascalCase()) {
            camelToPascalCase(fieldName)
        } else {
            fieldName
        }
        return "${objectRef.build(c)}.$finalFieldName"
    }
}
typealias GetterFieldAccessBuilderOps = GetterFieldAccessBuilder.() -> Unit
fun getterFieldAccess(ops: GetterFieldAccessBuilderOps) = GetterFieldAccessBuilder().apply(ops)

fun instanceVariable(name: String) = expression { c ->
    "${c.lang.softThis()}$name"
}

fun const(value: Int) = expression(value.toString())
fun const(value: String) = expression(value)

fun nullValue() = expression { c -> c.lang.nullValue() }
fun string(value: String) = expression("\"$value\"")
fun emptyString() = string("")
fun emptyBuilderValue() = expression { c -> c.lang.emptyBuilderValue() }

class PlusBuilder: ExpressionBuilder {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder

    override fun build(c: CodeBuilderContext): String {
        return "${left.build(c)} + ${right.build(c)}"
    }
}
typealias PlusBuilderOps = PlusBuilder.() -> Unit
fun plus(ops: PlusBuilderOps): PlusBuilder {
    return PlusBuilder().apply(ops)
}

typealias StringProvider = () -> String
fun comment(comment: StringProvider) = object : StatementBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("// ${comment()}")
    }
}

class IsEqualToArgs {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder
}
fun isEqualTo(block: IsEqualToArgs.() -> Unit) = expression { c ->
    val args = IsEqualToArgs().apply(block)
    "${args.left.build(c)} == ${args.right.build(c)}"
}