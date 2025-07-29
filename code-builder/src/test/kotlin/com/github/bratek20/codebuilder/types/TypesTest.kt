package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import org.junit.jupiter.api.Nested
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
                add(baseType(BaseType.BOOL))
                lineEnd()

                lineStart()
                add(baseType(BaseType.ANY))
                lineEnd()

                lineStart()
                add(baseType(BaseType.VOID))
                lineEnd()

                lineStart()
                add(baseType(BaseType.DOUBLE))
                lineEnd()

                lineStart()
                add(baseType(BaseType.LONG))
                lineEnd()
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    Int
                    String
                    Boolean
                    Any
                    Unit
                    Double
                    Long
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    number
                    string
                    boolean
                    any
                    void
                    number
                    number
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    int
                    string
                    bool
                    object
                    void
                    double
                    long
                """
            }
        }
    }

    @Test
    fun pairType() {
        testLinePartOps {
            ops {
                add(pairType(typeName("SomeType"), baseType(BaseType.STRING)))

                add(newPair("varA", "varB"))
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



    @Nested
    inner class ListScope {
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
        fun allLanguagesOps() {
            testOp {
                op = {
                    add(assignment {
                        left = variableDeclaration {
                            type = baseType(BaseType.STRING)
                            name = "firstElem"
                        }
                        right = listOp(variable("list")).get(const(0))
                    })
                    add(assignment {
                        left = variableDeclaration {
                            type = mutableListType(baseType(BaseType.STRING))
                            name = "list"
                        }
                        right = emptyMutableList(baseType(BaseType.STRING))
                    })

                    add(listOp(variable("list")).add {
                        string("someString")
                    })


                    lineStart()
                    add(listOp(variable("list")).find {
                        isEqualTo {
                            left = variable("it")
                            right = variable("other")
                        }
                    })
                    lineEnd()

                    lineStart()
                    add(listOp(variable("list")).map {
                        plus {
                            left = variable("it")
                            right = const(1)
                        }
                    })
                    lineEnd()
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
                   const list: string[] = []
                   list.push("someString")
                   list.find(it => it == other)
                   list.map(it => it + 1)
                """
                }
                langExpected {
                    lang = CSharp()
                    expected = """
                   string firstElem = list[0];
                   List<string> list = new List<string>();
                   list.Add("someString")
                   list.Find(it => it == other)
                   list.Select(it => it + 1).ToList()
                """
                }
            }
        }

        @Test
        fun newListOf() {
            testOp {
                op = {
                    add(assignment {
                        left = variableDeclaration {
                            type = listType(baseType(BaseType.STRING))
                            name = "list"
                        }
                        right = newListOf(
                            baseType(BaseType.STRING),
                            string("str1"),
                            string("str2")
                        )
                    })
                }
                langExpected {
                    lang = CSharp()
                    expected =
                    """
                    List<string> list = new List<string>() { "str1", "str2" };
                    """
                }
                langExpected {
                    lang = TypeScript()
                    expected =
                    """
                    const list: string[] = [ "str1", "str2" ]
                    """
                }
            }
        }
    }

    @Nested
    inner class ClassTypeScope {
        @Test
        fun onlyCSharp() {
            testOp {
                op = {
                    add(assignment {
                        left = variableDeclaration {
                            type = classType()
                            name = "someClassType"
                        }
                        right = typeOf(typeName("SomeClass"))
                    })
                }
                langExpected {
                    lang = CSharp()
                    expected =
                    """
                    Type someClassType = typeof(SomeClass);
                    """
                }
            }
        }
    }

    @Test
    fun optional() {
        testOp {
            op = {
                add(assignment {
                    left = variableDeclaration {
                        type = softOptionalType(baseType(BaseType.STRING))
                        name = "softOptional"
                    }
                    right = softOptional("someVariable")
                })

                add(assignment {
                    left = variableDeclaration {
                        type = hardOptionalType(baseType(BaseType.STRING))
                        name = "hardOptional"
                    }
                    right = hardOptional(baseType(BaseType.STRING)) {
                        variable("someVariable")
                    }
                })

                add(assignment {
                    left = variableDeclaration {
                        type = hardOptionalType(baseType(BaseType.STRING))
                        name = "emptyHardOptional"
                    }
                    right = emptyHardOptional(baseType(BaseType.STRING))
                })

                add(assignment {
                    left = variableDeclaration {
                        type = baseType(BaseType.STRING)
                        name = "unpacked"
                    }
                    right = optionalOp(variable("optional")).get()
                })

                add(assignment {
                    left = variableDeclaration {
                        type = softOptionalType(baseType(BaseType.INT))
                        name = "unpackedToSoft"
                    }
                    right = optionalOp(variable("optional")).orElse {
                        const(1)
                    }
                })

                add(assignment {
                    left = variableDeclaration {
                        type = softOptionalType(baseType(BaseType.STRING))
                        name = "unpackedToSoftDefaultNull"
                    }
                    right = optionalOp(variable("optional")).orElse {
                        nullValue()
                    }
                })

                add(assignment {
                    left = variableDeclaration {
                        type = baseType(BaseType.INT)
                        name = "plusOne"
                    }
                    right = optionalOp(variable("optional")).map {
                        plus {
                            left = variable("it")
                            right = const(1)
                        }
                    }
                })
                add(assignment {
                    left = variable("x")
                    right = nullCoalescing {
                        left = variable("y")
                        defaultValue = const(1)
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                   val softOptional: String? = someVariable
                   val hardOptional: String? = someVariable
                   val emptyHardOptional: String? = null
                   val unpacked: String = optional!!
                   val unpackedToSoft: Int? = optional ?: 1
                   val unpackedToSoftDefaultNull: String? = optional
                   val plusOne: Int = optional?.let { it -> it + 1 }
                   x = y ?? (1)
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                   const softOptional: string | undefined = someVariable
                   const hardOptional: Optional<string> = Optional.of(someVariable)
                   const emptyHardOptional: Optional<string> = Optional.empty()
                   const unpacked: string = optional.get()
                   const unpackedToSoft: number | undefined = optional.orElse(1)
                   const unpackedToSoftDefaultNull: string | undefined = optional.orElse(undefined)
                   const plusOne: number = optional.map(it => it + 1)
                   x = y ?? (1)
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                   string? softOptional = someVariable;
                   Optional<string> hardOptional = Optional<string>.Of(someVariable);
                   Optional<string> emptyHardOptional = Optional<string>.Empty();
                   string unpacked = optional.Get();
                   int? unpackedToSoft = optional.OrElse(1);
                   string? unpackedToSoftDefaultNull = optional.OrElse(null);
                   int plusOne = optional.Map(it => it + 1);
                   x = y ?? (1);
                """
            }
        }
    }

    @Nested
    inner class LambdaTypeScope {
        @Test
        fun `empty lambda`() {
            testLinePartOps {
                ops {
                    add(emptyLambda())
                    add(emptyLambda(1))
                    add(emptyLambda(2))
                }
                langExpected {
                    lang = Kotlin()
                    expected = """
                    {}
                    {}
                    {}
                    """
                }
                langExpected {
                    lang = TypeScript()
                    expected = """
                    {}
                    {}
                    {}
                    """
                }
                langExpected {
                    lang = CSharp()
                    expected = """
                    () => {}
                    (_) => {}
                    (_1, _2) => {}
                    """
                }
            }
        }

        @Test
        fun `lambda builder`() {
            testLinePartOps {
                ops {
                    add(lambda {
                        addArg {
                            name = "arg"
                            type = baseType(BaseType.INT)
                        }
                        body = methodCall {
                            target = variable("mock")
                            methodName = "someMethod"
                            addArg {
                                variable("arg")
                            }
                        }
                    })
                }
                langExpected {
                    lang = TypeScript()
                    expected = """
                    (arg: number) => { mock.someMethod(arg) }
                    """
                }
            }
        }

        @Test
        fun `lambda type`() {
            testLinePartOps {
                ops {
                    add(lambdaType(typeName("SomeType")))
                }
                langExpected {
                    lang = CSharp()
                    expected = """
                    Action<SomeType>
                    """
                }
                langExpected {
                    lang = Kotlin()
                    expected = """
                    (SomeType.() -> Unit)
                    """
                }
            }
        }

        @Test
        fun `lambda call`() {
            testLinePartOps {
                ops {
                    add(lambdaCall {
                        name = "someLambda"
                        addArg {
                            variable("arg")
                        }
                    })
                }
                langExpected {
                    lang = CSharp()
                    expected = """
                    someLambda.Invoke(arg)
                    """
                }
            }
        }
    }
}