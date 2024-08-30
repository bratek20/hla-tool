package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class TypeScriptFileBuilderTest {
    @Test
    fun namespace() {
        testOp {
            op = {
                typeScriptFile {
                    namespace {
                        name = "SomeNamespace"
                        addClass {
                            name = "SomeClass"
                        }
                    }

                    addClass {
                        name = "OuterClass"
                    }
                }
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    namespace SomeNamespace {
                        export class SomeClass {
                        }
                    }
                    class OuterClass {
                    }
                """
            }
        }
    }

    @Test
    fun functionCall() {
        testOp {
            op = {
                typeScriptFile {
                    addFunctionCall {
                        name = "someFun"
                    }
                }
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    someFun()
                """
            }
        }
    }
}