package com.github.bratek20.codebuilder.core

import org.junit.jupiter.api.Test

class CodeBuilderTest {
    @Test
    fun `should support lines and tab operations`() {
        testCodeBuilderOp {
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
        testCodeBuilderOp {
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
        testCodeBuilderOp {
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
        testCodeBuilderOp {
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
        testCodeBuilderOpException {
            op = {
                line("First line")
                linePart("x")
            }
            expectedMessage = "linePart(\"x\") failed - line not started! Previous line: First line"
        }

        testCodeBuilderOpException {
            op = {
                line("First line")
                lineStart("a")
                lineStart("b")
            }
            expectedMessage = "lineStart(\"b\") failed - line already started, current value: \"a\"! Previous full line: \"First line\""
        }

        testCodeBuilderOpException {
            op = {
                lineEnd("a")
            }
            expectedMessage = "lineEnd(\"a\") failed - line not started! Previous line: "
        }

        testCodeBuilderOpException {
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
        testCodeBuilderOp {
            op = {
                add {
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
}