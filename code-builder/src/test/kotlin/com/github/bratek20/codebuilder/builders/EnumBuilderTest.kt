package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class EnumBuilderTest {
    @Test
    fun `should work`() {
        testOp {
            op = {
                enum {
                    name = "SomeEnum"

                    addValue("A")
                    addValue("B")
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    enum class SomeEnum {
                        A,
                        B,
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public enum SomeEnum {
                        A,
                        B,
                    }
                """
            }
        }
    }
}