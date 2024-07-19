package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Test

class OpsTest {
    @Test
    fun opsExamples() {
        testCodeBuilderOp {
            op = {
                assign {
                    variable = "variable"
                    value = {
                        plus {
                            left = {
                                const("1")
                            }
                            right = {
                                const("2")
                            }
                        }
                    }
                }

                lineStart()
                isEqualTo {
                    left = { variable("a") }
                    right = { variable("b") }
                }
                lineEnd()

                returnBlock {
                    variable("a")
                }
            }
            expected = """
                variable = 1 + 2
                a == b
                return a
            """
        }
    }
}