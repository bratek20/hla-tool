package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.ops.plus
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.types.baseType
import org.junit.jupiter.api.Test

class FunctionBuilderTest {
    @Test
    fun `empty function`() {
        testCodeBuilderOp {
            op = {
                add(function { name = "someFunction" })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun someFunction() {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    function someFunction() {
                    }
                """
            }
        }
    }

    @Test
    fun `sum function`() {
        testCodeBuilderOp {
            op = {
                add(function {
                    name = "sum"
                    addArg {
                        name = "a"
                        type = baseType(BaseType.INT)
                    }
                    addArg {
                        name = "b"
                        type = baseType(BaseType.INT)
                    }
                    returnType = baseType(BaseType.INT)
                    body = {
                        returnBlock {
                            plus {
                                left = { variable("a") }
                                right = { variable("b") }
                            }
                        }
                    }
                })
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
                    function sum(a: number, b: number): number {
                        return a + b
                    }
                """
            }
        }
    }
}