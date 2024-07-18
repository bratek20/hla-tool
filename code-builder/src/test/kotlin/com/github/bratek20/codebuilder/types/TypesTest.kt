package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testCodeBuilderOp
import com.github.bratek20.codebuilder.ops.string
import com.github.bratek20.codebuilder.ops.variable
import org.junit.jupiter.api.Test

class TypesTest {
    @Test
    fun baseTypes() {
        testCodeBuilderOp {
            op = {
                lineStart()
                add(baseType(BaseType.INT))
                lineEnd()

                lineStart()
                add(baseType(BaseType.STRING))
                lineEnd()

                lineStart()
                add(baseType(BaseType.BOOLEAN))
                lineEnd()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   Int
                   String
                   Boolean
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   number
                   string
                   boolean
                """
            }
        }
    }

    @Test
    fun pairType() {
        testCodeBuilderOp {
            op = {
                lineStart()
                add(pairType(type("SomeType"), baseType(BaseType.STRING)))
                lineEnd()

                lineStart()
                newPair("varA", "varB")
                lineEnd()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   Pair<SomeType, String>
                   Pair(varA, varB)
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   [SomeType, string]
                   [varA, varB]
                """
            }
        }
    }

    @Test
    fun pairOps() {
        testCodeBuilderOp {
            op = {
                lineStart()
                add(pairOp("pair").first())
                lineEnd()

                lineStart()
                add(pairOp("pair").second())
                lineEnd()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   pair.first
                   pair.second
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   pair[0]
                   pair[1]
                """
            }
        }
    }

    @Test
    fun listType() {
        testCodeBuilderOp {
            op = {
                lineStart()
                add(listType(type("SomeType")))
                lineEnd()

                lineStart()
                add(mutableListType(type("SomeType")))
                lineEnd()

                lineStart()
                add(emptyMutableList())
                lineEnd()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   List<SomeType>
                   MutableList<SomeType>
                   mutableListOf()
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   SomeType[]
                   SomeType[]
                   []
                """
            }
        }
    }

    @Test
    fun listOps() {
        val list = emptyList<String>()
        list.find { it -> it == "someString" }

        testCodeBuilderOp {
            op = {
                lineStart()
                listOp("list").get(0)
                lineEnd()

                listOp("list").add {
                    variable("someVar")
                }

                listOp("list").add {
                    string("someString")
                }

                listOp("list").find {
                    it.isEqualTo {
                        variable("other")
                    }
                }
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   list[0]
                   list.add(someVar)
                   list.add("someString")
                   list.find { it -> it == other }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   list[0]
                   list.push(someVar)
                   list.push("someString")
                   list.find( it => it == other )
                """
            }
        }
    }
}