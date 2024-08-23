package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class CSharpFileBuilderTest {
    @Test
    fun namespace() {
        testCodeBuilderOp {
            op = {
                cSharpFile {
                    namespace {
                        name = "SomeNamespace"
                        addClass {
                            name = "SomeClass"
                        }
                    }
                }
            }
            langExpected {
                lang = CSharp()
                expected = """
                    namespace SomeNamespace {
                        public class SomeClass {
                        }
                    }
                """
            }
        }
    }
}