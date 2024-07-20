package com.github.bratek20.codebuilder.typescript

import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NamespaceBuilderTest {
    @Test
    fun `should work`() {
        testCodeBuilderOp {
            op = {
                namespace {
                    name = "SomeNamespace"
                    classBlock {
                        name = "SomeClass"
                    }
                    function {
                        name = "someFunction"
                    }
                }
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    namespace SomeNamespace {
                        export class SomeClass {
                        }
                        export function someFunction() {
                        }
                    }
                """
            }
        }
    }
}