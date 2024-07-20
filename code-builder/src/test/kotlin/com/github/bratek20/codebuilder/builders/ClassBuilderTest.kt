package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.ops.comment
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
import org.junit.jupiter.api.Test

class ClassBuilderTest {

    @Test
    fun `empty class`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                }
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
                classBlock {
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

    @Test
    fun `class with comment and method`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    body = {
                        comment("some comment")
                        method {
                            name = "someMethod"
                        }
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        // some comment
                        fun someMethod() {
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `class with fields split by empty line`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    body = {
                        field {
                            accessor = FieldAccessor.PRIVATE
                            name = "a"
                            type = type("A")
                            value = linePartBlock("null")
                        }
                        emptyLine()
                        field {
                            name = "b"
                            type = type("B")
                        }
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        private val a: A = null
                    
                        val b: B
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        private readonly a: A = null
                    
                        readonly b: B
                    }
                """
            }
        }
    }


    @Test
    fun `constructor field`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    constructorField {
                        name = "id"
                        type = baseType(BaseType.STRING)
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass(
                        val id: String,
                    ) {
                    }
                """
            }
        }
    }

    @Test
    fun `static methods`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    staticMethod {
                        name = "someMethod"
                    }
                    staticMethod {
                        name = "otherMethod"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        companion object {
                            fun someMethod() {
                            }
                            fun otherMethod() {
                            }
                        }
                    }
                """
            }
        }
    }

    @Test
    fun complicatedClass() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeInterfaceSomeCommandRequest"
                    constructorField {
                        accessor = FieldAccessor.PRIVATE
                        name = "id"
                        type = baseType(BaseType.STRING)
                    }
                    constructorField {
                        accessor = FieldAccessor.PRIVATE
                        name = "amount"
                        type = baseType(BaseType.INT)
                    }
                    body = {
                        method {
                            name = "getId"
                            returnType = type("SomeId")
                            body = {
                                returnBlock {
                                    constructorCall {
                                        className = "SomeId"
                                        addArg {
                                            variable("id")
                                        }
                                    }
                                }
                            }
                        }
                        method {
                            name = "getAmount"
                            returnType = baseType(BaseType.INT)
                            body = {
                                returnBlock {
                                    variable("amount")
                                }
                            }
                        }
                    }
                    staticMethod {
                        name = "create"
                        returnType = type("SomeInterfaceSomeCommandRequest")
                        addArg {
                            type = type("SomeId")
                            name = "id"
                        }
                        addArg {
                            type = baseType(BaseType.INT)
                            name = "amount"
                        }
                        body = {
                            returnBlock {
                                constructorCall {
                                    className = "SomeInterfaceSomeCommandRequest"
                                    addArg {
                                        variable("id.value")
                                    }
                                    addArg {
                                        variable("amount")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeInterfaceSomeCommandRequest(
                        private val id: String,
                        private val amount: Int,
                    ) {
                        fun getId(): SomeId {
                            return SomeId(id)
                        }
                        fun getAmount(): Int {
                            return amount
                        }
                        companion object {
                            fun create(id: SomeId, amount: Int): SomeInterfaceSomeCommandRequest {
                                return SomeInterfaceSomeCommandRequest(id.value, amount)
                            }
                        }
                    }
                """
            }
        }
    }
}