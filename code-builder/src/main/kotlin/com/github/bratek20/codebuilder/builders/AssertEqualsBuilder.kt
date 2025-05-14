package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class AssertEqualsBuilder: StatementBuilder {
    lateinit var given: ExpressionBuilder
    lateinit var expected: ExpressionBuilder
    lateinit var message: ExpressionBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps  {
        return when(c.lang) {
            is Kotlin -> buildKotlin()
            is TypeScript -> buildTypeScript()
            else -> throw IllegalArgumentException("Unsupported language: ${c.lang.name()}")
        }
    }

    private fun buildKotlin(): CodeBuilderOps = {
        lineStart()
        linePart("assertThat(")
        add(given)
        linePart(").withFailMessage(")
        add(message)
        linePart(").isEqualTo(")
        add(expected)
        linePart(")")
        statementLineEnd()
    }

    private fun buildTypeScript(): CodeBuilderOps = {
        lineStart()
        linePart("AssertEquals(")
        add(given)
        linePart(", ")
        add(expected)
        linePart(", ")
        add(message)
        linePart(")")
        statementLineEnd()
    }
}

typealias AssertEqualsBuilderOps = AssertEqualsBuilder.() -> Unit
fun assertEquals(block: AssertEqualsBuilderOps) = AssertEqualsBuilder().apply(block)