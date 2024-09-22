package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.pairOp
import com.github.bratek20.codebuilder.types.pairType
import com.github.bratek20.codebuilder.types.typeName
import org.junit.jupiter.api.Test

class ProcedureBuildersTest {
    @Test
    fun `empty method`() {
        testOp {
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
    fun `empty function`() {
        testOp {
            op = {
                add(function {
                    name = "someFunction"
                })
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
        testOp {
            op = {
                add(function {
                    name = "someFunction"
                    setBody {
                        addFunctionCall {
                            name = "someOtherFunction"
                        }
                        add(assignment {
                            left = variable("a")
                            right = variable("1")
                        })
                        add(returnStatement {
                            variable("a")
                        })
                    }
                })
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
        testOp {
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
                    setBody {
                        add(returnStatement {
                            plus {
                                left = variable("a")
                                right = variable("b")
                            }
                        })
                    }
                })
                add(assignment {
                    left = variable("result")
                    right = methodCall {
                        methodName = "sum"

                        addArg {
                            const(1)
                        }
                        addArg {
                            const(2)
                        }
                    }
                })
                add(assignment {
                    left = variable("sumOfSum")
                    right = plus {
                        left = methodCall {
                            target = variable("left")
                            methodName = "sum"

                            addArg {
                                const(1)
                            }
                            addArg {
                                const(2)
                            }
                        }
                        right = methodCall {
                            target = variable("right")
                            methodName = "sum"

                            addArg {
                                const(3)
                            }
                            addArg {
                                const(4)
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
                    result = sum(1, 2)
                    sumOfSum = left.sum(1, 2) + right.sum(3, 4)
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    sum(a: number, b: number): number {
                        return a + b
                    }
                    result = sum(1, 2)
                    sumOfSum = left.sum(1, 2) + right.sum(3, 4)
                """
            }
        }
    }

    @Test
    fun `pair arg`() {
        testOp {
            op = {
                add(method {
                    name = "sumPair"
                    addArg {
                        name = "p"
                        type = pairType(baseType(BaseType.INT), baseType(BaseType.INT))
                    }
                    returnType = baseType(BaseType.INT)
                    setBody {
                        add(returnStatement {
                            plus {
                                left = pairOp("p").first()
                                right = pairOp("p").second()
                            }
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

    @Test
    fun defaultArg() {
        testOp {
            op = {
                add(method {
                    name = "defaultArg"
                    addArg {
                        name = "a"
                        type = baseType(BaseType.INT)
                        defaultValue = "5"
                    }
                    returnType = baseType(BaseType.INT)
                })
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

    @Test
    fun genericType() {
        testOp {
            op = {
                add(method {
                    name = "someFun"
                    addGeneric("SomeType")
                })
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public void SomeFun<SomeType>() {
                    }
                """
            }
        }
    }

    @Test
    fun protectedMethod() {
        testOp {
            op = {
                add(method {
                    name = "someFun"
                    modifier = AccessModifier.PROTECTED
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    protected fun someFun() {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    protected someFun() {
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    protected void SomeFun() {
                    }
                """
            }
        }
    }
}