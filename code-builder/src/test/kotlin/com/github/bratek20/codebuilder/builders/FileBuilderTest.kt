package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class FileBuilderTest {
    @Test
    fun `two classes`() {
        testCodeBuilderOp {
            op = {
                file {
                    addClass {
                        name = "Class1"
                    }
                    addClass {
                        name = "Class2"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    
                    class Class1 {
                    }
                    
                    class Class2 {
                    }
                """
            }
        }
    }

    @Test
    fun `all methods`() {
        testCodeBuilderOp {
            op = {
                file {
                    addClass {
                        name = "SomeClass"
                    }

                    addFunction {
                        name = "someFun"
                    }

                    addEnum {
                        name = "SomeEnum"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    
                    class SomeClass {
                    }
                    
                    fun someFun() {
                    }
                    
                    enum class SomeEnum {
                    }
                """
            }
        }
    }

    @Test
    fun `should keep add order`() {
        testCodeBuilderOp {
            op = {
                file {
                    addClass {
                        name = "SomeClass"
                    }

                    addFunction {
                        name = "someFun"
                    }

                    addClass {
                        name = "OtherClass"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    
                    class SomeClass {
                    }
                    
                    fun someFun() {
                    }
                    
                    class OtherClass {
                    }
                """
            }
        }
    }
}