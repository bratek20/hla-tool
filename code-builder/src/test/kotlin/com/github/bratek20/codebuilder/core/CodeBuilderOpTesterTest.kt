package com.github.bratek20.codebuilder.core

import org.junit.jupiter.api.Test

class CodeBuilderOpTesterTest {
    @Test
    fun `should do nothing if single line string used`() {
        testOp {
            op = {
                line("val x = 1")
            }
            expected = "val x = 1"
        }
    }

    @Test
    fun `should align indent when multiline string used`() {
        testOp {
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

    @Test
    fun `should align empty tabs`() {
        testOp {
            op = {
                line("val x = 1")
                emptyLine()
                line("val y = 2")
            }
            //note tab in line 2 between val x and val y
            expected = """
                val x = 1
                    
                val y = 2
            """
        }
    }
}