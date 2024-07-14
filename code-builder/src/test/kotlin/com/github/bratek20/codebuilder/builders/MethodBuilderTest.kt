package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.Kotlin
import com.github.bratek20.codebuilder.TypeScript
import com.github.bratek20.codebuilder.testClassOp
import org.junit.jupiter.api.Test

class MethodBuilderTest {
    @Test
    fun `empty method`() {
        testClassOp {
            op = {
                addMethod {}
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    fun someMethod() {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someMethod() {
                    }
                """
            }
        }
    }
}