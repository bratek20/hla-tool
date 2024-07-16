package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class FunctionBuilderTest {
    @Test
    fun `empty function`() {
        testCodeBuilderOp {
            op = {
                add(function { name = "someFunction" })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun someFunction() {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    function someFunction() {
                    }
                """
            }
        }
    }
}