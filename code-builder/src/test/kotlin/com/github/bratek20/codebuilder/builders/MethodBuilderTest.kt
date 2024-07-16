package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.ops.plus
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.pairOp
import com.github.bratek20.codebuilder.types.pairType
import org.junit.jupiter.api.Test

class MethodBuilderTest {
    @Test
    fun `empty method`() {
        testCodeBuilderOp {
            op = {
                add(method {
                    name = "someMethod"
                })
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
        testCodeBuilderOp {
            op = {
                add(method {
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
                        add(returnBlock {
                            add(plus {
                                left = linePartBlock("a")
                                right = linePartBlock("b")
                            })
                        })
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
                    sum(a: number, b: number): number {
                        return a + b
                    }
                """
            }
        }
    }

    @Test
    fun `pair arg`() {
        testCodeBuilderOp {
            op = {
                add(method {
                    name = "sumPair"
                    addArg {
                        name = "p"
                        type = pairType(baseType(BaseType.INT), baseType(BaseType.INT))
                    }
                    returnType = baseType(BaseType.INT)
                    body = {
                        add(returnBlock {
                            add(plus {
                                left = pairOp("p").first()
                                right = pairOp("p").second()
                            })
                        })
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun sumPair(p: Pair<Int, Int>): Int {
                        return p.first + p.second
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    sumPair(p: [number, number]): number {
                        return p[0] + p[1]
                    }
                """
            }
        }
    }
}