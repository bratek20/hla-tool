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
    fun `should throw exceptions when line manipulation used badly`() {
        testCodeBuilderOpException {
            op = {
                line("First line")
                linePart("x")
            }
            expectedMessage = "linePart() without lineStart() is not allowed, previous line: First line"
        }

        testCodeBuilderOpException {
            op = {
                line("First line")
                lineStart("a")
                lineStart("b")
            }
            expectedMessage = "Line start failed for `b` - line already started! Previous full line: `First line`, already started line: `a`"
        }

        testCodeBuilderOpException {
            op = {
                lineEnd()
            }
            expectedMessage = "lineEnd() without lineStart() is not allowed, previous line: "
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