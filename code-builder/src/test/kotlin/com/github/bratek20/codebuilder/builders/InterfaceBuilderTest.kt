package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Test

class InterfaceBuilderTest {

    @Test
    fun `empty interface`() {
        testOp {
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
    fun `method comment`() {
        testOp {
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
                    interface SomeInterface {
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
                        someMethod()
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public interface SomeInterface {
                        // some comment
                        void SomeMethod();
                    }
                """
            }
        }
    }

    @Test
    fun `method throws documentation`() {
        testOp {
            op = {
                interfaceBlock {
                    name = "SomeInterface"

                    addMethod {
                        name = "oneExceptionMethod"
                        addThrows("SomeException")
                    }
                    addMethod {
                        name = "twoExceptionsMethod"
                        addThrows("SomeException")
                        addThrows("AnotherException")
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    interface SomeInterface {
                        @Throws(
                            SomeException::class,
                        )
                        fun oneExceptionMethod()
                    
                        @Throws(
                            SomeException::class,
                            AnotherException::class,
                        )
                        fun twoExceptionsMethod()
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    interface SomeInterface {
                        /**
                        * @throws { SomeException }
                        */
                        oneExceptionMethod()
                    
                        /**
                        * @throws { SomeException }
                        * @throws { AnotherException }
                        */
                        twoExceptionsMethod()
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public interface SomeInterface {
                        /// <exception cref="SomeException"/>
                        void OneExceptionMethod();
                    
                        /// <exception cref="SomeException"/>
                        /// <exception cref="AnotherException"/>
                        void TwoExceptionsMethod();
                    }
                """
            }
        }
    }

    @Test
    fun `two methods`() {
        testOp {
            op = {
                interfaceBlock {
                    name = "SomeInterface"

                    addMethod {
                        name = "method1"
                    }
                    addMethod {
                        name = "method2"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    interface SomeInterface {
                        fun method1()
                    
                        fun method2()
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    interface SomeInterface {
                        method1()
                    
                        method2()
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public interface SomeInterface {
                        void Method1();
                    
                        void Method2();
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