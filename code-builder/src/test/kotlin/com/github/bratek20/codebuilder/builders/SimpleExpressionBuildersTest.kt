package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import com.github.bratek20.codebuilder.types.typeName
import org.junit.jupiter.api.Test

class SimpleExpressionBuildersTest {
    @Test
    fun `variable assignment`() {
        testOp {
            op = {
                add(assignment {
                    left = variableDeclaration {
                        mutable = true
                        type = typeName("SomeType")
                        name = "someVariable"
                    }
                    right = variable("someOtherVariable")
                })
                add(assignment {
                    left = variable("someVariable")
                    right = variable("someOtherVariable")
                })
                add(assignment {
                    left = variable("someVariable")
                    right = functionCall {
                        name = "someFunction"
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    var someVariable: SomeType = someOtherVariable
                    someVariable = someOtherVariable
                    someVariable = someFunction()
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    let someVariable: SomeType = someOtherVariable
                    someVariable = someOtherVariable
                    someVariable = someFunction()
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    SomeType someVariable = someOtherVariable;
                    someVariable = someOtherVariable;
                    someVariable = someFunction();
                """
            }
        }
    }
}