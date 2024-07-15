package com.github.bratek20.codebuilder

import com.github.bratek20.codebuilder.builders.ClassBuilder
import org.assertj.core.api.Assertions

class ExpectedLanguageString {
    lateinit var lang: CodeBuilderLanguage
    lateinit var expected: String
}
class CodeBuilderOpTester {
    lateinit var op: CodeBuilder.() -> Unit
    var lang: CodeBuilderLanguage? = null
    var expected: String? = null
    private val expectedLanguageStrings = mutableListOf<ExpectedLanguageString>()

    fun langExpected(init: ExpectedLanguageString.() -> Unit) {
        val expectedLanguageString = ExpectedLanguageString().apply(init)
        expectedLanguageStrings.add(expectedLanguageString)
    }

    fun test() {
        expected?.let {
            val language = lang ?: Kotlin()
            langExpected {
                this.expected = it
                this.lang = language
            }
        }

        expectedLanguageStrings.forEach { testForLang(it) }
    }

    private fun testForLang(langExpected: ExpectedLanguageString) {
        val result = CodeBuilder(langExpected.lang).apply(op).build()

        val expected = langExpected.expected

        val finalExpected = if (expected.contains("\n"))
            alignMultilineStringIndent(expected)
        else
            expected

        Assertions.assertThat(result)
            .withFailMessage("Failed for language ${langExpected.lang.name()}\nExpected:\n$finalExpected\nActual:\n$result")
            .isEqualTo(finalExpected)
    }
}
fun testCodeBuilderOp(init: CodeBuilderOpTester.() -> Unit) {
    CodeBuilderOpTester().apply(init).test()
}

class ClassBuilderOpTester {
    lateinit var op: ClassBuilder.() -> Unit
    private val opTester = CodeBuilderOpTester()

    fun langExpected(init: ExpectedLanguageString.() -> Unit) {
        opTester.langExpected(init)
    }

    fun test() {
        opTester.op = {
            ClassBuilder()
                .apply(op)
                .applyBodyOperations(this)
        }
        opTester.test()
    }
}
fun testClassOp(init: ClassBuilderOpTester.() -> Unit) {
    ClassBuilderOpTester().apply(init).test()
}

private fun alignMultilineStringIndent(str: String): String {
    val lines = str.lines().drop(1).dropLast(1)
    val smallestIndent = lines
        .map { it.takeWhile { c -> c.isWhitespace() } }
        .map { it.length }
        .minOrNull() ?: 0

    return lines.map { it.drop(smallestIndent) }.joinToString("\n")
}