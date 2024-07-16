package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
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
                        name = "a"
                        type = type("A")
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
                        val a: A
                    
                        val b: B
                    }
                """
            }
        }
    }
}