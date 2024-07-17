package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class OpsTest {
    @Test
    fun opsExamples() {
        testCodeBuilderOp {
            op = {
                returnBlock {
                    variable("1")
                }
                endLinePart()

                plus {
                    left = variable("1")
                    right = variable("2")
                }
                endLinePart()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    return 1
                    1 + 2
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    return 1
                    1 + 2
                """
            }
        }
    }
}