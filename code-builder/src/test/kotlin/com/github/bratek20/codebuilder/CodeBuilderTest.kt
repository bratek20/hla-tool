package com.github.bratek20.codebuilder

import org.junit.jupiter.api.Test

class CodeBuilderTest {
    @Test
    fun `should create one line block`() {
        testCodeBuilderOp {
            op = {
                line("val x = 1")
            }
            expected = "val x = 1"
        }
    }

}