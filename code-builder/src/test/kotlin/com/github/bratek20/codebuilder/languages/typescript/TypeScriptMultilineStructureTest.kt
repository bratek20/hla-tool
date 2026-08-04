package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.methodCallStatement
import com.github.bratek20.codebuilder.builders.string
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class TypeScriptMultilineStructureTest {
    @Test
    fun `multiline structure with expression and arrow function properties`() {
        testOp {
            op = {
                lineStart("someVariable = someFunction(")
                add(typeScriptMultilineStructure {
                    addProperty {
                        key = "someKey"
                        value = string("someValue")
                    }
                    addProperty {
                        key = "someLambda"
                        blockValue = typeScriptArrowFunction {
                            argName = "builder"
                            setBody {
                                add(methodCallStatement {
                                    target = variable("builder")
                                    name = "with"
                                    addArg {
                                        variable("someArg")
                                    }
                                })
                            }
                        }
                    }
                })
                lineSoftEnd(").someField")
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someVariable = someFunction({
                        someKey: "someValue",
                        someLambda: builder => {
                            builder.with(someArg)
                        }
                    }).someField
                """
            }
        }
    }
}
