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