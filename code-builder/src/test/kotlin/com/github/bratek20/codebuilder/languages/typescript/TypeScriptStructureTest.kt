package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class TypeScriptStructureTest {
    @Test
    fun shouldWork() {
        testOp {
            op = {
                add(typeScriptStructure {

                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    {
                    }
                """
            }
        }
    }
}