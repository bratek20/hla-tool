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
                baseType(BaseType.INT)
                endLinePart()

                baseType(BaseType.STRING)
                endLinePart()

                baseType(BaseType.BOOLEAN)
                endLinePart()
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
                add(pairType(type("SomeType"), baseType(BaseType.STRING)))
                endLinePart()

                add(newPair("varA", "varB"))
                endLinePart()
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
                add(pairOp("pair").first())
                endLinePart()

                add(pairOp("pair").second())
                endLinePart()
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
                add(listType(type("SomeType")))
                endLinePart()

                add(mutableListType(type("SomeType")))
                endLinePart()

                add(emptyMutableList())
                endLinePart()
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
        testCodeBuilderOp {
            op = {
                add(listOp("list").get(0))
                endLinePart()

                add(listOp("list").add {
                    variable("someVar")
                })
                endLinePart()

                add(listOp("list").add {
                    string("someString")
                })
                endLinePart()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   list[0]
                   list.add(someVar)
                   list.add("someString")
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   list[0]
                   list.push(someVar)
                   list.push("someString")
                """
            }
        }
    }
}