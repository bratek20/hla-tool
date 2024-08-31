package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Test

class TypesTest {
    @Test
    fun baseTypes() {
        testOp {
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
        testOp {
            op = {
                lineStart()
                add(pairType(typeName("SomeType"), baseType(BaseType.STRING)))
                lineEnd()

                lineStart()
                legacyNewPair("varA", "varB")
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
        testOp {
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
        testOp {
            op = {
                lineStart()
                add(listType(typeName("SomeType")))
                lineEnd()

                lineStart()
                add(mutableListType(typeName("SomeType")))
                lineEnd()

                lineStart()
                add(emptyMutableList(typeName("SomeType")))
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
        testOp {
            op = {
                add(assignment {
                    left = variableDeclaration {
                        type = typeName("String")
                        name = "firstElem"
                    }
                    right = listOp("list").get(0)
                })
                add(assignment {
                    left = variableDeclaration {
                        type = mutableListType(typeName("String"))
                        name = "list"
                    }
                    right = emptyMutableList(typeName("String"))
                })

                add(listOp("list").add {
                    variable("someVar")
                })

                add(listOp("list").add {
                    string("someString")
                })

                add(listOp("list").find {
                    isEqualTo {
                        left = variable("it")
                        right = variable("other")
                    }
                })

                add(listOp("list").map {
                    plus {
                        left = variable("it")
                        right = const(1)
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   val firstElem: String = list[0]
                   val list: MutableList<String> = mutableListOf()
                   list.add("someString")
                   list.find { it -> it == other }
                   list.map { it -> it + 1 }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   const firstElem: string = list[0]
                   list.push(someVar)
                   list.push("someString")
                   list.find( it => it == other )
                   list.map( it => it + 1 )
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                   List<string> firstElem = list[0]
                   list.Add(someVar)
                   list.Add("someString")
                   list.Find( it => it == other )
                   list.Select( it => it + 1 )
                """
            }
        }
    }

    @Test
    fun optional() {
        testOp {
            op = {
                add(assignment {
                    left = variableDeclaration {
                        type = typeName("string")
                        name = "someVariable"
                    }
                    right = variable("someVariable")
                })

                add(optionalOp("optional").map {
                    plus {
                        left = variable("it")
                        right = const(1)
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   val softOptional: string? = someVariable
                   val hardOptional: string? = someVariable
                   val unpacked: string = optional!!
                   val unpackedToSoft: string? = optional ?: null
                   optional.map { it -> it + 1 }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   const softOptional: string? = someVariable
                   const hardOptional: Optional<string> = Optional.of(someVariable)
                   const unpacked: string = optional.get()
                   const unpackedToSoft: string? = optional.orElse(undefined)
                   optional.map( it => it + 1 )
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                   string? softOptional = someVariable
                   Optional<string> hardOptional = Optional.Of<string>(someVariable)
                   string unpacked = optional.Get()
                   string? unpackedToSoft = optional.OrElse(null)
                   optional.Map( it => it + 1 )
                """
            }
        }
    }
}