package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Test

class OpsTest {
    @Test
    fun opsExamples() {
        testCodeBuilderOp {
            op = {
                assign {
                    variable = {
                        name = "variable"
                        declare = true
                    }
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

                assign {
                    variable = {
                        name = "areEqual"
                        declare = true
                        mutable = true
                    }
                    value = {
                        isEqualTo {
                            left = {
                                variable("a")
                            }
                            right = {
                                variable("b")
                            }
                        }
                    }
                }

                assign {
                    variable = {
                        name = "areEqual"
                    }
                    value = {
                        const("false")
                    }
                }

                returnBlock {
                    variable("a")
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    val variable = 1 + 2
                    var areEqual = a == b
                    areEqual = false
                    return a
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    const variable = 1 + 2
                    let areEqual = a == b
                    areEqual = false
                    return a
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    var variable = 1 + 2;
                    var areEqual = a == b;
                    areEqual = false;
                    return a;
                """
            }
        }
    }
}