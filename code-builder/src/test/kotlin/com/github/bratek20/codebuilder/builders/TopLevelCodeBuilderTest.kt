package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class TopLevelCodeBuilderTest {
    @Test
    fun `all methods`() {
        testOp {
            op = {
                file {
                    addInterface {
                        name = "SomeInterface"
                    }

                    addClass {
                        name = "SomeClass"
                    }

                    addComment{
                        "Some comment"
                    }
                    addFunction {
                        name = "someFun"
                    }

                    addExtraEmptyLines(2)

                    addEnum {
                        name = "SomeEnum"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    interface SomeInterface {
                    }
                    
                    class SomeClass {
                    }
                    
                    // Some comment
                    fun someFun() {
                    }
                    
                    
                    
                    enum class SomeEnum {
                    }
                """
            }
        }
    }

    @Test
    fun `two classes`() {
        testOp {
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
    fun `should keep add order`() {
        testOp {
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