package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.pairOp
import com.github.bratek20.codebuilder.types.pairType
import org.junit.jupiter.api.Test

class MethodAndFunctionBuilderTest {
    @Test
    fun `empty method`() {
        testCodeBuilderOp {
            op = {
                legacyMethod {
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
    fun `empty function`() {
        testCodeBuilderOp {
            op = {
                function { name = "someFunction" }
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
    fun `function with body`() {
        testCodeBuilderOp {
            op = {
                function {
                    name = "someFunction"
                    body {
                        addFunctionCall {
                            name = "someOtherFunction"
                        }
                        addVariableAssignment {
                            name = "a"
                            value = variable("1")
                        }
                        addReturn(variable("a"))
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun someFunction() {
                        someOtherFunction()
                        a = 1
                        return a
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    function someFunction() {
                        someOtherFunction()
                        a = 1
                        return a
                    }
                """
            }
        }
    }


    @Test
    fun `sum method with calls`() {
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
                    body {
                        addReturn(
                            plus {
                                left = variable("a")
                                right = variable("b")
                            }
                        )
                    }
                })
                add(variableAssignment {
                    name = "result"
                    value = methodCall {
                        variableName = "this"
                        methodName = "sum"

                        addArgLegacy {
                            legacyConst("1")
                        }
                        addArgLegacy {
                            legacyConst("2")
                        }
                    }
                })
                add(variableAssignment {
                    name = "sumOfSum"
                    value = plus {
                        left = methodCall {
                            variableName = "left"
                            methodName = "sum"
                            skipSoftEnd = true

                            addArg(const(1))
                            addArg(const(2))
                        }
                        right = methodCall {
                            variableName = "right"
                            methodName = "sum"

                            addArg(const(3))
                            addArg(const(4))
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
                    result = this.sum(1, 2)
                    sumOfSum = left.sum(1, 2) + right.sum(3, 4)
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    sum(a: number, b: number): number {
                        return a + b
                    }
                    result = this.sum(1, 2)
                    sumOfSum = left.sum(1, 2) + right.sum(3, 4)
                """
            }
        }
    }

    @Test
    fun `pair arg`() {
        testCodeBuilderOp {
            op = {
                legacyMethod {
                    name = "sumPair"
                    addArg {
                        name = "p"
                        type = pairType(baseType(BaseType.INT), baseType(BaseType.INT))
                    }
                    returnType = baseType(BaseType.INT)
                    legacyBody = {
                        legacyReturn {
                            legacyPlus {
                                left = { pairOp("p").first() }
                                right = { pairOp("p").second() }
                            }
                        }
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

    @Test
    fun defaultArg() {
        testCodeBuilderOp {
            op = {
                legacyMethod {
                    name = "defaultArg"
                    addArg {
                        name = "a"
                        type = baseType(BaseType.INT)
                        defaultValue = "5"
                    }
                    returnType = baseType(BaseType.INT)
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun defaultArg(a: Int = 5): Int {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    defaultArg(a: number = 5): number {
                    }
                """
            }
        }
    }




}