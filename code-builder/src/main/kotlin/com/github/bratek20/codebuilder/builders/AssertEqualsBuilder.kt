package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class AssertEqualsBuilder: StatementBuilder {
    lateinit var given: ExpressionBuilder
    lateinit var expected: ExpressionBuilder
    lateinit var message: ExpressionBuilder

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps  {
        val langName = c.lang.name()
        return if(langName == Kotlin().name()) {
            buildKotlin()
        } else if(langName == TypeScript().name()) {
            buildTypeScript()
        } else {
            throw IllegalArgumentException("Unsupported language: $langName")
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