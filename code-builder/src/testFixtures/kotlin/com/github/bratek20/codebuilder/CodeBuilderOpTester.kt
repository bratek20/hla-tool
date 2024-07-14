package com.github.bratek20.codebuilder

import org.assertj.core.api.Assertions

class TestBlockBuilderArgs {
    lateinit var op: CodeBuilder.() -> Unit
    lateinit var expected: String
    var lang: CodeBuilderLanguage = Kotlin()
}
fun testCodeBuilderOp(init: TestBlockBuilderArgs.() -> Unit) {
    val args = TestBlockBuilderArgs().apply(init)
    val result = CodeBuilder(args.lang).apply(args.op).build()

    val expected = args.expected

    val finalExpected = if (expected.contains("\n"))
        alignMultilineStringIndent(expected)
    else
        expected

    Assertions.assertThat(result).isEqualTo(finalExpected)
}

private fun alignMultilineStringIndent(str: String): String {
    val lines = str.lines().drop(1).dropLast(1)
    val smallestIndent = lines
        .map { it.takeWhile { c -> c.isWhitespace() } }
        .map { it.length }
        .minOrNull() ?: 0

    return lines.map { it.drop(smallestIndent) }.joinToString("\n")
}