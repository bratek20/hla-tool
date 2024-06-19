package pl.bratek20.hla.parsing

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import com.github.bratek20.architecture.context.someContextBuilder
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.api.TypeWrapper
import pl.bratek20.hla.definitions.fixtures.assertModules
import pl.bratek20.hla.parsing.api.ModuleDefinitionsParser
import pl.bratek20.hla.parsing.impl.ParsingContextModule

class ModuleDefinitionsParserTest {
    private val parser = someContextBuilder()
        .withModule(ParsingContextModule())
        .build()
        .get(ModuleDefinitionsParser::class.java)

    private fun parse(pathSuffix: String): List<ModuleDefinition> {
        val fullPath = "src/test/resources/parsing/$pathSuffix"
        return parser.parse(Path(fullPath))
    }

    @Test
    fun `should parse two modules`() {
        val modules = parse("two-modules")

        assertModules(modules, listOf(
            {
                name = "OtherModule"
                simpleValueObjects = listOf {
                    name = "OtherId"
                    typeName = "string"
                }
                complexValueObjects = listOf {
                    name = "OtherClass"
                    fields = listOf(
                        {
                            name = "id"
                            type = {
                                name = "OtherId"
                            }
                        },
                        {
                            name = "amount"
                            type = {
                                name = "int"
                            }
                        }
                    )
                }
            },
            {
                name = "SomeModule"
                simpleValueObjects = listOf {
                    name = "SomeId"
                    typeName = "string"
                }
                complexValueObjects = listOf(
                    {
                        name = "SomeClass"
                        fields = listOf(
                            {
                                name = "id"
                                type = {
                                    name = "SomeId"
                                }
                            },
                            {
                                name = "amount"
                                type = {
                                    name = "int"
                                }
                                attributes = listOf {
                                    name = "public"
                                    value = "true"
                                }
                            }
                        )
                    },
                    {
                        name = "SomeClass2"
                        fields = listOf(
                            {
                                name = "id"
                                type = {
                                    name = "SomeId"
                                }
                            },
                            {
                                name = "names"
                                type = {
                                    name = "string"
                                    wrappers = listOf(
                                        TypeWrapper.LIST
                                    )
                                }
                            },
                            {
                                name = "ids"
                                type = {
                                    name = "SomeId"
                                    wrappers = listOf(
                                        TypeWrapper.LIST
                                    )
                                }
                            },
                            {
                                name = "enabled"
                                type = {
                                    name = "bool"
                                }
                                defaultValue = "true"
                            },
                        )
                    },
                    {
                        name = "SomeClass3"
                        fields = listOf(
                            {
                                name = "class2Object"
                                type = {
                                    name = "SomeClass2"
                                }
                            },
                            {
                                name = "class2List"
                                type = {
                                    name = "SomeClass2"
                                    wrappers = listOf(
                                        TypeWrapper.LIST
                                    )
                                }
                            }
                        )
                    },
                    {
                        name = "SomeClass4"
                        fields = listOf(
                            {
                                name = "otherId"
                                type = {
                                    name = "OtherId"
                                }
                            },
                            {
                                name = "otherClass"
                                type = {
                                    name = "OtherClass"
                                }
                            },
                            {
                                name = "otherIdList"
                                type = {
                                    name = "OtherId"
                                    wrappers = listOf(
                                        TypeWrapper.LIST
                                    )
                                }
                            },
                            {
                                name = "otherClassList"
                                type = {
                                    name = "OtherClass"
                                    wrappers = listOf(
                                        TypeWrapper.LIST
                                    )
                                }
                            }
                        )
                    },
                    {
                        name = "SomeClass5"
                        fields = listOf {
                            name = "otherId"
                            type = {
                                name = "OtherId"
                                wrappers = listOf(
                                    TypeWrapper.OPTIONAL
                                )
                            }
                        }
                    }
                )
                interfaces = listOf {
                    name = "SomeInterface"
                    methods = listOf (
                        {
                            name = "someCommand"
                            returnType = {
                                name = "void"
                            }
                            args = listOf (
                                {
                                    name = "id"
                                    type = {
                                        name = "SomeId"
                                    }
                                },
                                {
                                    name = "amount"
                                    type = {
                                        name = "int"
                                    }
                                }
                            )
                            throws = listOf (
                                {
                                    name = "SomeException"
                                },
                                {
                                    name = "SomeException2"
                                }
                            )
                        },
                        {
                            name = "someQuery"
                            returnType = {
                                name = "SomeClass"
                            }
                            args = listOf {
                                name = "id"
                                type = {
                                    name = "SomeId"
                                }
                            }
                        },
                        {
                            name = "noArgQuery"
                            returnType = {
                                name = "SomeClass"
                            }
                            args = emptyList()
                        }
                    )
                }
                enums = listOf {
                    name = "SomeEnum"
                    values = listOf(
                        "VALUE_A",
                        "VALUE_B"
                    )
                }
            }
        ))
    }

    @Test
    fun `should parse property and data keys`() {
        val modules = parse("only-keys")

        assertModules(modules, listOf {
            propertyKeys = listOf(
                {
                    name = "someElements"
                    type = {
                        name = "SomeElement"
                        wrappers = listOf(
                            TypeWrapper.LIST
                        )
                    }
                },
                {
                    name = "someConfig"
                    type = {
                        name = "SomeProperty"
                    }
                }
            )
            dataKeys = listOf(
                {
                    name = "someElements"
                    type = {
                        name = "SomeElement"
                        wrappers = listOf(
                            TypeWrapper.LIST
                        )
                    }
                },
                {
                    name = "someConfig"
                    type = {
                        name = "SomeProperty"
                    }
                }
            )
        })
    }

    @Test
    fun `should parse custom types`() {
        val modules = parse("only-custom-types")

        assertModules(modules, listOf {
            simpleCustomTypes = listOf {
                name = "SimpleType"
                typeName = "string"
                attributes = listOf {
                    name = "example"
                    value = "\"0:0\""
                }
            }
            complexCustomTypes = listOf {
                name = "ComplexType"
                fields = listOf (
                    {
                        name = "field1"
                        type = {
                            name = "string"
                        }
                        attributes = listOf {
                            name = "example"
                            value = "\"abc\""
                        }
                    },
                    {
                        name = "field2"
                        type = {
                            name = "int"
                        }
                        attributes = listOf {
                            name = "example"
                            value = "123"
                        }
                    }
                )
            }
        })
    }

    @Test
    fun `should parse module with comments`() {
        val modules = parse("comments")

        assertModules(modules, listOf {
            name = "SomeModule"
            simpleValueObjects = listOf {
                name = "SomeId"
                typeName = "string"
            }
        })
    }

    @Test
    fun `should not crash for bug`() {
        assertThatCode{
            parse("bug")
        }.doesNotThrowAnyException()
    }

    @Test
    fun `should parse data`() {
        val modules = parse("only-data")

        assertModules(modules, listOf {
            dataClasses = listOf(
                {
                    name = "SomeData"
                    fields = listOf {
                        name = "value"
                        type = {
                            name = "int"
                        }
                    }
                },
                {
                    name = "SomeElementData"
                    fields = listOf {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    }
                },
            )
            dataKeys = listOf {
                name = "someElements"
                type = {
                    name = "SomeElementData"
                    wrappers = listOf(
                        TypeWrapper.LIST
                    )
                }
            }
            implSubmodule = {
                dataClasses = listOf {
                    name = "SomeImplData"
                    fields = listOf {
                        name = "value"
                        type = {
                            name = "bool"
                        }
                    }
                }
                dataKeys = listOf {
                    name = "someImplData"
                    type = {
                        name = "SomeImplData"
                        wrappers = emptyList()
                    }
                }
            }
        })
    }
}