package pl.bratek20.hla.parsing

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import pl.bratek20.architecture.context.someContextBuilder
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
                namedTypes = listOf {
                    name = "OtherId"
                    typeName = "string"
                }
                valueObjects = listOf {
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
                namedTypes = listOf {
                    name = "SomeId"
                    typeName = "string"
                }
                valueObjects = listOf(
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
                                name = "enabled"
                                type = {
                                    name = "bool"
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
                            }
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
    fun `should parse properties`() {
        val modules = parse("only-properties")

        assertModules(modules, listOf {
            properties = listOf(
                {
                    name = "SomeProperty"
                    fields = listOf {
                        name = "value"
                        type = {
                            name = "string"
                        }
                    }
                },
                {
                    name = "SomeElement"
                    fields = listOf {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    }
                },
                {
                    name = "SomeConfig"
                    fields = listOf {
                        name = "enabled"
                        type = {
                            name = "bool"
                        }
                    }
                }
            )
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
                        name = "SomeConfig"
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
            }
            complexCustomTypes = listOf {
                name = "ComplexType"
                fields = listOf (
                    {
                        name = "field1"
                        type = {
                            name = "string"
                        }
                    },
                    {
                        name = "field2"
                        type = {
                            name = "int"
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
            namedTypes = listOf {
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
}