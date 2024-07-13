package com.github.bratek20.hla.codebuilder

import com.github.bratek20.codebuilder.testBlockBuilder
import org.junit.jupiter.api.Test

class BlockBuilderTesterTest {
    @Test
    fun `should do nothing if single line string used`() {
        testBlockBuilder {
            block = OneLineBlock("val x = 1")
            expected = "val x = 1"
        }
    }

    @Test
    fun `should align indent when multiline string used`() {
        testBlockBuilder {
            block = ManyCodeBlocksSeparatedByLine(
                listOf(
                    OneLineBlock("val x = 1"),
                    OneLineBlock("val y = 2")
                )
            )
            expected = """
                val x = 1
                
                val y = 2
            """
        }
    }
}