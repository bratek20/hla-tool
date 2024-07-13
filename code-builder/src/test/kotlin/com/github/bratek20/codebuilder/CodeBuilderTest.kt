package com.github.bratek20.codebuilder

import com.github.bratek20.codebuilder.testBlockBuilder
import org.junit.jupiter.api.Test

class CodeBuilderTest {
    @Test
    fun `should create one line block`() {
        testBlockBuilder {
            block = OneLineBlock("val x = 1")
            expected = "val x = 1"
        }
    }

    @Test
    fun `empty class Kotlin`() {
        testBlockBuilder {
            block = Class(
                className = "SomeClass",
                implementedInterfaceName = "SomeInterface",
                body = EmptyBlock()
            )
            lang = Kotlin()
            expected = """
                class SomeClass: SomeInterface {
                }
            """
        }
    }

    @Test
    fun `empty class TypeScript`() {
        testBlockBuilder {
            block = Class(
                className = "SomeClass",
                implementedInterfaceName = "SomeInterface",
                body = EmptyBlock()
            )
            lang = TypeScript()
            expected = """
                class SomeClass implements SomeInterface {
                }
            """
        }
    }
}