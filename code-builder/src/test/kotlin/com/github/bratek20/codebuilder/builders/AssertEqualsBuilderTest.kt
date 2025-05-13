package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class AssertEqualsBuilderTest {
    @Test
    fun `generate assert equals based on language`() {
        testOp {
            op = {
                add(assertEquals {
                    message = string("someMessage")
                    given = expression("otherMethodCalls")
                    expected = expression("expectedNumber")
                })
            }
            langExpected {
                lang = Kotlin()
                expected = "assertThat(otherMethodCalls).withFailMessage(\"someMessage\").isEqualTo(expectedNumber)"
            }
            langExpected {
                lang = TypeScript()
                expected = "AssertEquals(otherMethodCalls, expectedNumber, \"someMessage\")"
            }
        }
    }
}