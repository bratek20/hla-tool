package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.ops.const
import com.github.bratek20.codebuilder.ops.plus
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
            langExpected {
                lang = CSharp()
                expected = """
                   int
                   string
                   bool
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
            langExpected {
                lang = CSharp()
                expected = """
                   Tuple<SomeType, string>
                   Tuple.Create(varA, varB)
                """
            }
        }
    }

    @Test
    fun pairOps() {
        testCodeBuilderOp {
            op = {
                lineStart()
                pairOp("pair").first()
                lineEnd()

                lineStart()
                pairOp("pair").second()
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
            langExpected {
                lang = CSharp()
                expected = """
                   pair.Item1
                   pair.Item2
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
                add(emptyMutableList(type("SomeType")))
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
            langExpected {
                lang = CSharp()
                expected = """
                   List<SomeType>
                   List<SomeType>
                   new List<SomeType>()
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

                lineStart()
                listOp("list").find {
                    it.isEqualTo {
                        variable("other")
                    }
                }

                lineStart()
                listOp("list").map {
                    plus {
                        left = { variable(it.name) }
                        right = { const("1") }
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
                   list.map { it -> it + 1 }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   list[0]
                   list.push(someVar)
                   list.push("someString")
                   list.find( it => it == other )
                   list.map( it => it + 1 )
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                   list[0]
                   list.Add(someVar)
                   list.Add("someString")
                   list.Find( it => it == other )
                   list.Select( it => it + 1 )
                """
            }
        }
    }
}