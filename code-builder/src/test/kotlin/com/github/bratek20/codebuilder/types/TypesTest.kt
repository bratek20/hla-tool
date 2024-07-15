package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.BaseType
import com.github.bratek20.codebuilder.Kotlin
import com.github.bratek20.codebuilder.TypeScript
import com.github.bratek20.codebuilder.testCodeBuilderOp
import org.junit.jupiter.api.Test

class TypesTest {
    @Test
    fun baseTypes() {
        testCodeBuilderOp {
            op = {
                add(baseType(BaseType.INT))
                endLinePart()

                add(baseType(BaseType.STRING))
                endLinePart()

                add(baseType(BaseType.BOOLEAN))
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
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   Pair<SomeType, String>
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   [SomeType, string]
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
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   List<SomeType>
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   SomeType[]
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

                add(listOp("list").add("\"someValue\""))
                endLinePart()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   list[0]
                   list.add("someValue")
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   list[0]
                   list.push("someValue")
                """
            }
        }
    }
}