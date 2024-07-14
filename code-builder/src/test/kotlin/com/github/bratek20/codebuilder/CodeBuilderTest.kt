package com.github.bratek20.codebuilder

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

                startLinePart("val")
                addLinePart(" y")
                addLinePart(" = 2")

                untab()
                line("}")
            }
            expected = """
                {
                    val x = 1
                
                    val y = 2
                }
            """
        }
    }
}