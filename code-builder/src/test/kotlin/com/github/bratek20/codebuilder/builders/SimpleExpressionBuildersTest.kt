package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class SimpleExpressionBuildersTest {
    @Test
    fun `variable assignment`() {
        testCodeBuilderOp {
            op = {
                add(variableAssignment {
                    name = "someVariable"
                    declare = true
                    mutable = true
                    value = variable("someOtherVariable")
                })
                add(variableAssignment {
                    name = "someVariable"
                    value = variable("someOtherVariable")
                })
                add(variableAssignment {
                    name = "someVariable"
                    value = functionCall {
                        name = "someFunction"
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    var someVariable = someOtherVariable
                    someVariable = someOtherVariable
                    someVariable = someFunction()
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    let someVariable = someOtherVariable
                    someVariable = someOtherVariable
                    someVariable = someFunction()
                """
            }
        }
    }
}