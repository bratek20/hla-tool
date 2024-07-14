package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*
import org.junit.jupiter.api.Test

class MethodBuilderTest {
    @Test
    fun `empty method`() {
        testClassOp {
            op = {
                method {
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
                method {
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
                    body = {
                        line("return a + b")
                    }
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

    @Test
    fun `pair arg`() {
        testClassOp {
            op = {
                method {
                    name = "sumPair"
                    addArg {
                        name = "p"
                        type = {
                            pair = {
                                first = {
                                    base = BaseType.INT
                                }
                                second = {
                                    base = BaseType.INT
                                }
                            }
                        }
                    }
                    returnType = {
                        base = BaseType.INT
                    }
                    body = {
                        linePart("return ")
                        pairFirst("p")
                        linePart(" + ")
                        pairSecond("p")
                    }
                }
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