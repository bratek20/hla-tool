package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.Kotlin
import com.github.bratek20.codebuilder.TypeScript
import com.github.bratek20.codebuilder.testCodeBuilderOp
import org.junit.jupiter.api.Test

class ClassBuilderTest {

    @Test
    fun `empty class`() {
        testCodeBuilderOp {
            op = {
                addClass {}
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                    }
                """
            }
        }
    }

    @Test
    fun `class with empty body that implements interface`() {
        testCodeBuilderOp {
            op = {
                addClass {
                    name = "SomeClass"
                    implementedInterfaceName = "SomeInterface"
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass: SomeInterface {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass implements SomeInterface {
                    }
                """
            }
        }
    }
}