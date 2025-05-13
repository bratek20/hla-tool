package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class AssertEqualsBuilder: StatementBuilder {
    lateinit var given: ExpressionBuilder
    lateinit var expected: ExpressionBuilder
    lateinit var message: String

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val langName = c.lang.name()
        lineStart()
        linePart(if(langName == TypeScript().name()) {
            "AssertEquals("
        } else {
            "assertThat("
        })
        add(given)
        if(langName == Kotlin().name()) {
            linePart(").withFailMessage(\"$message\").isEqualTo(")
        }else if(langName == TypeScript().name()) {
            linePart(", ")
        }
        add(expected)
        if(langName == TypeScript().name()) {
            linePart(", $message")
        }
        linePart(")")
        statementLineEnd()
    }
}

typealias AssertEqualsBuilderOps = AssertEqualsBuilder.() -> Unit
fun assertEquals(block: AssertEqualsBuilderOps) = AssertEqualsBuilder().apply(block)