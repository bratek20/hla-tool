package com.github.bratek20.hla.parsing

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.definitions.fixtures.assertModuleGroups
import com.github.bratek20.hla.definitions.fixtures.assertModules
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupsParser
import com.github.bratek20.hla.parsing.api.UnknownRootSectionException
import com.github.bratek20.hla.parsing.impl.ParsingContextModule

class ModuleGroupsParserTest {
    private val parser = someContextBuilder()
        .withModule(ParsingContextModule())
        .build()
        .get(ModuleGroupsParser::class.java)

    private fun parseSingleGroup(pathSuffix: String): List<ModuleDefinition> {
        val fullPath = "src/test/resources/parsing/$pathSuffix"
        return parser.parse(Path(fullPath), ProfileName("test")) // all properties.yaml are copy-pasted
            .flatMap { it.getModules() }
    }

    private fun parse(pathSuffix: String, profileName: String): List<ModuleGroup> {
        val fullPath = "src/test/resources/parsing/$pathSuffix"
        return parser.parse(Path(fullPath), ProfileName(profileName))
    }

    @Test
    fun `should parse two modules`() {
        val modules = parseSingleGroup("two-modules")

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
                    methods = listOf(
                        {
                            name = "someCommand"
                            returnType = {
                                name = "void"
                            }
                            args = listOf(
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
                            throws = listOf(
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
        val modules = parseSingleGroup("only-keys")

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
        val modules = parseSingleGroup("only-custom-types")

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
                fields = listOf(
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
        val modules = parseSingleGroup("comments")

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
        assertThatCode {
            parseSingleGroup("bug")
        }.doesNotThrowAnyException()
    }

    @Test
    fun `should parse data`() {
        val modules = parseSingleGroup("only-data")

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

    @Test
    fun `should parse external types`() {
        val modules = parseSingleGroup("external-types")

        assertModules(modules, listOf {
            externalTypes = listOf(
                "LegacyType",
            )
            kotlinConfig = {
                externalTypePackages = listOf {
                    name = "LegacyType"
                    packageName = "com.some.pkg.legacy"
                }
            }
        })
    }

    @Test
    fun `should throw exception if root level section is unknown`() {
        assertApiExceptionThrown(
            { parseSingleGroup("unknown-section") },
            {
                type = UnknownRootSectionException::class
                message = "Module SomeModule has unknown root sections: [SomeUnknownSection, SomeUnknownSection2]"
            }
        )
    }

    @Test
    fun `should parse other module groups modules imported by given`() {
        val groups = parse("imports/group2", "group2Profile")

        assertModuleGroups(groups, listOf(
            {
                name = "group2"
                modules = listOf {
                    name = "Group2Module"
                }
                profile = {
                    name = "group2Profile"
                }
            },
            {
                name = "group1"
                modules = listOf {
                    name = "Group1Module"
                }
                profile = {
                    name = "group1Profile"
                }
            }
        ))
    }
}