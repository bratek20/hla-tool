package com.github.bratek20.codebuilder.core

import org.junit.jupiter.api.Test

class CodeBuilderTest {
    @Test
    fun `should support lines and tab operations`() {
        testOp {
            op = {
                line("{")
                tab()

                line("val x = 1")

                emptyLine()

                lineStart("val")
                linePart(" y")
                lineEnd(" = 2")

                line("// something")

                untab()
                line("}")
            }
            expected = """
                {
                    val x = 1
                
                    val y = 2
                    // something
                }
            """
        }
    }

    @Test
    fun `lineSoftStart() - can start line but can also be used for started line`() {
        testOp {
            op = {
                lineSoftStart("a")
                lineEnd()

                lineStart("1")
                linePart("2")
                lineSoftStart()
                lineSoftStart("3")
                lineEnd()
            }
            expected = """
                a
                123
            """
        }
    }

    @Test
    fun `lineSoftEnd() - allows for new line start but does not end line instantly`() {
        testOp {
            op = {
                lineStart("a")
                lineSoftEnd("b")

                lineStart("1")
                lineSoftEnd("2")
                linePart("3")
                lineSoftEnd()
                lineEnd("4")
            }
            expected = """
                ab
                1234
            """
        }
    }

    @Test
    fun `lineSoftStart() should start new line if lineSoftEnd() was used`() {
        testOp {
            op = {
                lineStart("a")
                lineSoftEnd("b")
                lineSoftStart("c")
                lineEnd()
            }
            expected = """
                ab
                c
            """
        }
    }

    @Test
    fun `should throw exceptions when line manipulation used badly`() {
        testOpException {
            op = {
                line("First line")
                linePart("x")
            }
            expectedMessage = "linePart(\"x\") failed - line not started! Previous line: First line"
        }

        testOpException {
            op = {
                line("First line")
                lineStart("a")
                lineStart("b")
            }
            expectedMessage = "lineStart(\"b\") failed - line already started, current value: \"a\"! Previous full line: \"First line\""
        }

        testOpException {
            op = {
                lineEnd("a")
            }
            expectedMessage = "lineEnd(\"a\") failed - line not started! Previous line: "
        }

        testOpException {
            op = {
                lineSoftEnd("a")
            }
            expectedMessage = "lineSoftEnd(\"a\") failed - line not started! Previous line: "
        }
    }

    private fun codeBlockBuilderLangNamePrinter(): CodeBlockBuilder = object: CodeBlockBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
            line(c.lang.name())
        }
    }

    @Test
    fun `should support add addMany and context reading`() {
        testOp {
            op = {
                addOps {
                    line("x")
                }
                add(codeBlockBuilderLangNamePrinter())

                lineStart()
                add(linePartBlock("1"))
                add(linePartBlock("2"))
                lineEnd()

                addMany(
                    listOf("a", "b").map { lineBlock(it) }
                )
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    x
                    Kotlin
                    12
                    a
                    b
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    x
                    TypeScript
                    12
                    a
                    b
                """
            }
        }
    }


    private fun failingCodeBlockBuilder(): CodeBlockBuilder = object: CodeBlockBuilder {
        override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {

        }

        override fun validate(c: CodeBuilderContext): ValidationResult {
            return ValidationResult.failure("Some error message")
        }
    }

    @Test
    fun `should throw validation exception if code block builder validate returns failure`() {
        testOpException {
            op = {
                add(failingCodeBlockBuilder())
            }
            expectedExceptionType = CodeBuilderValidationException::class.java
            expectedMessage = "Some error message"
        }
    }
}