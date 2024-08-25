package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.ops.comment
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.ops.string
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
import org.junit.jupiter.api.Test

class InterfaceBuilderTest {

    @Test
    fun `empty interface`() {
        testCodeBuilderOp {
            op = {
                interfaceBlock {
                    name = "SomeInterface"
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    interface SomeInterface {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    interface SomeInterface {
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public interface SomeInterface {
                    }
                """
            }
        }
    }

    @Test
    fun `interface with comment and method`() {
        testCodeBuilderOp {
            op = {
                interfaceBlock {
                    name = "SomeInterface"

                    addMethod {
                        comment = "some comment"
                        name = "someMethod"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeInterface {
                        // some comment
                        fun someMethod()
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    interface SomeInterface {
                        // some comment
                        someMethod(): void
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public interface SomeInterface {
                        // some comment
                        void someMethod();
                    }
                """
            }
        }
    }

/*
    @Test
    fun `class extension`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    extends {
                        className = "SomeParent"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass: SomeParent {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass extends SomeParent {
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
                    implements = "SomeInterface"
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
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass: SomeInterface {
                    }
                """
            }
        }
    }



    @Test
    fun `extension with generic`() {
        testCodeBuilderOp {
            op = {
                classBlock {
                    name = "SomeClass"
                    extends {
                        className = "SomeParent"
                        generic = type("SomeType")
                    }
                }
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass extends SomeParent<SomeType> {
                    }
                """
            }
        }
    }
*/
}