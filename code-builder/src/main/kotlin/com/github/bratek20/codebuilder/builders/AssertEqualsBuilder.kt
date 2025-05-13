package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class AssertEqualsBuilder: StatementBuilder {
    lateinit var given: String
    lateinit var expected: String
    lateinit var message: String

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val langName = c.lang.name()
        val linePart = if( langName== TypeScript().name()) {
            "AssertEquals($given, $expected, \"$message\")"
        } else if(langName == Kotlin().name()) {
            "assertThat($given).withFailMessage(\"$message\").isEqualTo($expected)"
        }else {
            ""
        }
        lineStart()
        linePart(linePart)
        statementLineEnd()
    }
}

typealias AssertEqualsBuilderOps = AssertEqualsBuilder.() -> Unit
fun assertEquals(block: AssertEqualsBuilderOps) = AssertEqualsBuilder().apply(block)