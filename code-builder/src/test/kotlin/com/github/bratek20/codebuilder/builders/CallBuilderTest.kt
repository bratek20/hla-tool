package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testLinePartOps
import org.junit.jupiter.api.Test

class CallBuilderTest {
    @Test
    fun functionCallWithVarArgs() {
        testLinePartOps {
            ops {
                add(functionCall {
                    name = "someFunction"
                    addArg {
                        variable("arg1")
                    }
                    addArg {
                        variable("arg2")
                    }
                    addArg {
                        variable("arg3")
                    }
                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someFunction(arg1, arg2, arg3)
                """
            }
        }
    }

    @Test
    fun methodCallWithGeneric() {
        testLinePartOps {
            ops {
                add(methodCall {
                    name = "someFunction"
                    addGeneric("SomeType")
                })
            }
            langExpected {
                lang = CSharp()
                expected = """
                    SomeFunction<SomeType>()
                """
            }
        }
    }
}