package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.const
import com.github.bratek20.codebuilder.builders.string
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testLinePartOps
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.newListOf
import org.junit.jupiter.api.Test

class TypeScriptStructureTest {
    @Test
    fun shouldWork() {
        testLinePartOps {
            ops {
                add(typeScriptStructure {
                    addProperty {
                        key = "someKey"
                        value = string("someValue")
                    }
                    addProperty {
                        key = "someList"
                        value = newListOf(
                            baseType(BaseType.ANY),
                            typeScriptStructure {
                                addProperty {
                                    key = "key"
                                    value = const(5)
                                }
                            }
                        )
                    }
                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    { someKey: "someValue", someList: [ { key: 5 } ] }
                """
            }
        }
    }
}