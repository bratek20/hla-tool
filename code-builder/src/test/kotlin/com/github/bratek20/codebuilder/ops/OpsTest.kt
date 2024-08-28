package com.github.bratek20.codebuilder.ops

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Test

class OpsTest {
    @Test
    fun opsExamples() {
        testCodeBuilderOp {
            op = {
                legacyAssign {
                    variable = {
                        name = "variable"
                        declare = true
                    }
                    value = {
                        legacyPlus {
                            left = {
                                legacyConst("1")
                            }
                            right = {
                                legacyConst("2")
                            }
                        }
                    }
                }

                legacyAssign {
                    variable = {
                        name = "areEqual"
                        declare = true
                        mutable = true
                    }
                    value = {
                        isEqualTo {
                            left = {
                                legacyVariable("a")
                            }
                            right = {
                                legacyVariable("b")
                            }
                        }
                    }
                }

                legacyAssign {
                    variable = {
                        name = "areEqual"
                    }
                    value = {
                        legacyConst("false")
                    }
                }

                legacyReturn {
                    legacyVariable("a")
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