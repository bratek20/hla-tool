package com.github.bratek20.codebuilder

import org.junit.jupiter.api.Test

class ClassTest {

    @Test
    fun `empty class Kotlin`() {
        testCodeBuilderOp {
            op = {
                addClass {
                    name = "SomeClass"
                    implementedInterfaceName = "SomeInterface"
                    body = EmptyBlock()
                }
            }
            lang = Kotlin()
            expected = """
                class SomeClass: SomeInterface {
                }
            """
        }
    }

    @Test
    fun `empty class TypeScript`() {
        testCodeBuilderOp {
            op = {
                addClass {
                    name = "SomeClass"
                    implementedInterfaceName = "SomeInterface"
                    body = EmptyBlock()
                }
            }
            lang = TypeScript()
            expected = """
                class SomeClass implements SomeInterface {
                }
            """
        }
    }
}