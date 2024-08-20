package com.github.bratek20.codebuilder.kotlin

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class KotlinFileBuilderTest {
    @Test
    fun `two classes`() {
        testCodeBuilderOp {
            op = {
                kotlinFile {
                    packageName = "com.some.pkg"

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
                    package com.some.pkg
                    
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
                kotlinFile {
                    packageName = "com.some.pkg"

                    addImport("com.other.pkg1")
                    addImport("com.other.pkg2")

                    addClass {
                        name = "SomeClass"
                    }

                    addFunction {
                        name = "someFun"
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    package com.some.pkg
                    
                    import com.other.pkg1
                    import com.other.pkg2
                    
                    class SomeClass {
                    }
                    
                    fun someFun() {
                    }
                """
            }
        }
    }
}