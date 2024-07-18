package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.linePartBlock
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import com.github.bratek20.codebuilder.types.type
import org.junit.jupiter.api.Test

class ClassBuilderTest {

    @Test
    fun `empty class`() {
        testCodeBuilderOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                })
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
                add(classBlock {
                    name = "SomeClass"
                    implementedInterfaceName = "SomeInterface"
                })
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
                add(classBlock {
                    name = "SomeClass"
                    comment("some comment")
                    method {
                        name = "someMethod"
                    }
                })
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
                add(classBlock {
                    name = "SomeClass"
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
                })
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
}