package com.github.bratek20.codebuilder.languages.kotlin

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class KotlinFileBuilderTest {
    @Test
    fun `package name, imports and class`() {
        testOp {
            op = {
                kotlinFile {
                    packageName = "com.some.pkg"

                    addImport("com.other.pkg1")
                    addImport("com.other.pkg2")

                    addClass {
                        name = "SomeClass"
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
                """
            }
        }
    }
}