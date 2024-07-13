package com.github.bratek20.codebuilder

import com.github.bratek20.hla.codebuilder.CodeBlockBuilder
import com.github.bratek20.hla.codebuilder.CodeBuilder
import com.github.bratek20.hla.codebuilder.CodeBuilderLanguage
import com.github.bratek20.hla.codebuilder.Kotlin
import org.assertj.core.api.Assertions
import kotlin.math.exp

class TestBlockBuilderArgs(
    var block: CodeBlockBuilder? = null,
    var lang: CodeBuilderLanguage? = null,
    var expected: String? = null
)
fun testBlockBuilder(init: TestBlockBuilderArgs.() -> Unit) {
    val args = TestBlockBuilderArgs().apply(init)
    val result = CodeBuilder(args.lang ?: Kotlin())
        .add(args.block!!)
        .build()

    val expected = args.expected!!

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