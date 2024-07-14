package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.Kotlin
import com.github.bratek20.codebuilder.TypeScript
import com.github.bratek20.codebuilder.testCodeBuilderOp
import org.junit.jupiter.api.Test

class FunctionBuilderTest {
    @Test
    fun `empty function`() {
        testCodeBuilderOp {
            op = {
                addFunction {}
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