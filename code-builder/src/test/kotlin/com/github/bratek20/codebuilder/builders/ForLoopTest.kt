package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import org.junit.jupiter.api.Test

class ForLoopTest {
    @Test
    fun `should work`() {
        testOp {
            op = {
                add(forLoop(
                    from = const(0),
                    to = const(1),
                    body = { iVar ->
                        functionCallStatement {
                            name = "someFun"
                            addArg { iVar }
                        }
                    }
                ))
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   for (i in 0 until 1) {
                       someFun(i)
                   }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   for (let i = 0; i < 1; i++) {
                       someFun(i)
                   }
                """
            }
        }
    }
}