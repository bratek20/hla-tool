package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class CallBuilderTest {
    @Test
    fun functionCallWithVarArgs() {
        testCodeBuilderOp {
            op = {
                legacyFunctionCall {
                    name = "someFunction"
                    addArg(variable("arg1"))
                    addArg(variable("arg2"))
                    addArg(variable("arg3"))
                }
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someFunction(arg1, arg2, arg3)
                """
            }
        }
    }
}