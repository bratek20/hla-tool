package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import org.junit.jupiter.api.Test

class CSharpFileBuilderTest {
    @Test
    fun using() {
        testCodeBuilderOp {
            op = {
                cSharpFile {
                    addUsing("SomeNamespace")
                    addUsing("SomeOtherNamespace")
                }
            }
            langExpected {
                lang = CSharp()
                expected = """
                    using SomeNamespace;
                    using SomeOtherNamespace;
                    
                """
            }
        }
    }
    @Test
    fun namespace() {
        testCodeBuilderOp {
            op = {
                cSharpFile {
                    namespace("SomeNamespace")

                    addClass {
                        name = "SomeClass"
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