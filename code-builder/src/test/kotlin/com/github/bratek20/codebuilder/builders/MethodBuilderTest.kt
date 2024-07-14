package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*
import org.junit.jupiter.api.Test

class MethodBuilderTest {
    @Test
    fun `empty method`() {
        testClassOp {
            op = {
                addMethod {
                    name = "someMethod"
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun someMethod() {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someMethod() {
                    }
                """
            }
        }
    }

    @Test
    fun `sum method`() {
        testClassOp {
            op = {
                addMethod {
                    name = "sum"
                    addArg {
                        name = "a"
                        type = {
                            base = BaseType.INT
                        }
                    }
                    addArg {
                        name = "b"
                        type = {
                            base = BaseType.INT
                        }
                    }
                    returnType = {
                        base = BaseType.INT
                    }
                    body = OneLineBlock("return a + b")
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun sum(a: Int, b: Int): Int {
                        return a + b
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    sum(a: number, b: number): number {
                        return a + b
                    }
                """
            }
        }
    }
}