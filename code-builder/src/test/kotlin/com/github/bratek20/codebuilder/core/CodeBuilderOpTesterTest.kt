package com.github.bratek20.codebuilder.core

import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class CodeBuilderOpTesterTest {
    @Test
    fun `should do nothing if single line string used`() {
        testCodeBuilderOp {
            op = {
                line("val x = 1")
            }
            expected = "val x = 1"
        }
    }

    @Test
    fun `should align indent when multiline string used`() {
        testCodeBuilderOp {
            op = {
                line("val x = 1")
                emptyLine()
                line("val y = 2")
            }
            expected = """
                val x = 1
                
                val y = 2
            """
        }
    }
}