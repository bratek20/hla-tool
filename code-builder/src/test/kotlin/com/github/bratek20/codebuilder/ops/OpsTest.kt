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
                                variable("1")
                            }
                            right = {
                                variable("2")
                            }
                        }
                    }
                }

                returnBlock {
                    variable("a")
                }
            }
            expected = """
                variable = 1 + 2
                return a
            """
        }
    }
}