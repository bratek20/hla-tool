package com.github.bratek20.hla.parsing

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.definitions.fixtures.assertModuleDefinition
import com.github.bratek20.hla.definitions.fixtures.assertModules
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.parsing.api.UnknownRootSectionException
import com.github.bratek20.hla.parsing.context.ParsingImpl
import com.github.bratek20.hla.parsing.fixtures.assertModuleGroup
import com.github.bratek20.logs.LoggerMock
import com.github.bratek20.logs.LogsMocks
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ModuleGroupParserTest {
    private lateinit var parser: ModuleGroupParser
    private lateinit var loggerMock: LoggerMock

    @BeforeEach
    fun setUp() {
        val c = someContextBuilder()
            .withModules(
                ParsingImpl(),
                LogsMocks()
            )
            .build()

        parser = c.get(ModuleGroupParser::class.java)
        loggerMock = c.get(LoggerMock::class.java)
    }

    private fun parseSingleGroup(pathSuffix: String): List<ModuleDefinition> {
        val fullPath = "src/test/resources/parsing/$pathSuffix"
        return parser.parse(Path(fullPath), ProfileName("test")).getModules() // all properties.yaml are copy-pasted
    }

    private fun parseSingleModule(pathSuffix: String): ModuleDefinition {
        return parseSingleGroup(pathSuffix)[0]
    }

    private fun parse(pathSuffix: String, profileName: String): ModuleGroup {
        val fullPath = "src/test/resources/parsing/$pathSuffix"
        return parser.parse(Path(fullPath), ProfileName(profileName))
    }

    //useful to start crash investigation, feel free to modify crash/SomeModule.module
    @Test
    fun `should not crash for provided module`() {
        assertModuleDefinition(parseSingleModule("crash")) {
            interfaces = listOf {
                name = "SquadronWarsRepository"
                methods = listOf(
                    {
                        name = "findMatching"
                        args = listOf(
                            {
                                name = "warId"
                                type = {
                                    name = "SquadronWarId"
                                }
                            },
                            {
                                name = "squadronId"
                                type = {
                                    name = "SquadronId"
                                }
                            }
                        )
                        returnType = {
                            name = "SquadronWarMatchingData"
                            wrappers = listOf(
                                "OPTIONAL"
                            )
                        }
                    },
                    {
                        name = "findMatchingWithEmptySquadron2"
                        args = listOf(
                            {
                                name = "warId"
                                type = {
                                    name = "SquadronWarId"
                                }
                            }
                        )
                        returnType = {
                            name = "SquadronWarMatchingData"
                            wrappers = listOf(
                                "OPTIONAL"
                            )
                        }
                    },
                    {
                        name = "setMatching"
                        args = listOf(
                            {
                                name = "matching"
                                type = {
                                    name = "SquadronWarMatchingData"
                                }
                            }
                        )
                        returnType = {
                            name = "void"
                        }
                    }
                )
            }
        }

        loggerMock.assertErrors()
    }

    @Test
    fun `should parse two modules and log about it`() {
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
                                        "LIST"
                                    )
                                }
                            },
                            {
                                name = "ids"
                                type = {
                                    name = "SomeId"
                                    wrappers = listOf(
                                        "LIST"
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
                                        "LIST"
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
                                        "LIST"
                                    )
                                }
                            },
                            {
                                name = "otherClassList"
                                type = {
                                    name = "OtherClass"
                                    wrappers = listOf(
                                        "LIST"
                                    )
                                }
                            }
                        )
                    },
                    {
                        name = "SomeClass5"
                        fields = listOf(
                            {
                                name = "otherId"
                                type = {
                                    name = "OtherId"
                                    wrappers = listOf(
                                        "OPTIONAL"
                                    )
                                }
                            },
                            {
                                name = "optOtherIds"
                                type = {
                                    name = "OtherId"
                                    wrappers = listOf(
                                        "OPTIONAL",
                                        "LIST"
                                    )
                                }
                            }
                        )
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
                        },
                        {
                            name = "someOptionalQuery"
                            args = listOf {
                                name = "id"
                                type = {
                                    name = "SomeId"
                                    wrappers = listOf(
                                        "OPTIONAL"
                                    )
                                }
                            }
                            returnType = {
                                name = "SomeClass"
                                wrappers = listOf(
                                    "OPTIONAL"
                                )
                            }
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
                exceptions = listOf {
                    name = "SomeExtraException"
                }
                events = listOf {
                    name = "SomeEvent"
                    fields = listOf {
                        name = "someField"
                        type = {
                            name = "string"
                        }
                    }
                }
            }
        ))

        loggerMock.assertInfos(
            "Parsing group two-modules",
            "Parsing module OtherModule",
            "Parsing module SomeModule"
        )
    }

    @Test
    fun `should parse properties (keys and vos), data (keys and complex vos for api and impl), property keys and data keys`() {
        val module = parseSingleModule("data-and-properties")

        assertModuleDefinition(module) {
            propertyKeys = listOf(
                {
                    name = "someConfig"
                    type = {
                        name = "SomeConfig"
                    }
                },
                {
                    name = "someProperties"
                    type = {
                        name = "SomeProperty"
                        wrappers = listOf(
                            "LIST"
                        )
                    }
                },
            )
            simpleValueObjects = listOf {
                name = "SomeName"
                typeName = "string"
            }
            complexValueObjects = listOf(
                {
                    name = "SomeConfig"
                    fields = listOf {
                        name = "enabled"
                        type = {
                            name = "bool"
                        }
                    }
                },
                {
                    name = "SomeProperty"
                    fields = listOf {
                        name = "name"
                        type = {
                            name = "SomeName"
                        }
                    }
                },
            )
            dataKeys = listOf(
                {
                    name = "someData"
                    type = {
                        name = "SomeData"
                    }
                },
                {
                    name = "someElementsData"
                    type = {
                        name = "SomeElementData"
                        wrappers = listOf(
                            "LIST"
                        )
                    }
                },
            )
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
            implSubmodule = {
                dataKeys = listOf(
                    {
                        name = "someImplData"
                        type = {
                            name = "SomeImplData"
                        }
                    },
                    {
                        name = "someElementsImplData"
                        type = {
                            name = "SomeElementImplData"
                            wrappers = listOf(
                                "LIST"
                            )
                        }
                    },
                )
                dataClasses = listOf(
                    {
                        name = "SomeImplData"
                        fields = listOf {
                            name = "value"
                            type = {
                                name = "int"
                            }
                        }
                    },
                    {
                        name = "SomeElementImplData"
                        fields = listOf {
                            name = "id"
                            type = {
                                name = "SomeId"
                            }
                        }
                    },
                )
            }
        }
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
    fun `should handle tab as 4 spaces to avoid errors`() {
        assertModuleDefinition(parseSingleModule("tab-bug")) {
            complexValueObjects = listOf {
                name = "ClassWithTabbedField"
                fields = listOf {
                    name = "tabbedField"
                    type = {
                        name = "string"
                    }
                }
            }
        }

        loggerMock.assertErrors()
    }

    @Test
    fun `should parse kotlin config`() {
        val modules = parseSingleGroup("kotlin-config")

        assertModules(modules, listOf(
            {
                name = "Legacy"
                externalTypes = listOf(
                    "LegacyType",
                )
                kotlinConfig = {
                    externalTypePackages = listOf {
                        name = "LegacyType"
                        packageName = "com.some.pkg.legacy"
                    }
                }
            },
            {
                name = "Records"
                kotlinConfig = {
                    records = listOf(
                        "SomeClass"
                    )
                }
            }
        ))
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
        val groups = parse("imports/group3", "group3Profile")

        assertModuleGroup(groups) {
            name = "group3"
            modules = listOf {
                name = "Group3Module"
            }
            profile = {
                name = "group3Profile"
            }
            dependencies = listOf(
                {
                    name = "group2"
                    modules = listOf {
                        name = "Group2Module"
                    }
                    profile = {
                        name = "group2Profile"
                    }
                    dependencies = listOf {
                        name = "group1"
                        modules = listOf {
                            name = "Group1Module"
                        }
                        profile = {
                            name = "group1Profile"
                        }
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
                },
            )
        }
    }

    @Test
    fun `should parse value object from interfaces section`() {
        val module = parseSingleModule("interfaces-vos")

        assertModuleDefinition(module) {
            complexValueObjects = listOf(
                {
                    name = "SomeMethodInput"
                    fields = listOf {
                        name = "amount"
                        type = {
                            name = "int"
                        }
                    }
                },
                {
                    name = "SomeMethodOutput"
                    fields = listOf {
                        name = "name"
                        type = {
                            name = "string"
                        }
                    }
                }
            )
            interfaces = listOf {
                name = "SomeInterface"
                methods = listOf {
                    name = "someMethod"
                    args = listOf {
                        name = "input"
                        type = {
                            name = "SomeMethodInput"
                        }
                    }
                    returnType = {
                        name = "SomeMethodOutput"
                    }
                }
            }
        }
    }

    @Test
    fun `should parse web submodule`() {
        val modules = parseSingleGroup("web-submodule")

        assertModules(modules, listOf (
            {
                name = "OtherModule"
                webSubmodule = {
                    http = {
                        exposedInterfaces = listOf(
                            "OtherInterface"
                        )
                        serverNameEmpty = true
                        baseUrlEmpty = true
                        authEmpty = true
                        urlPathPrefixEmpty = true
                    }
                }
            },
            {
                name = "SomeModule"
                webSubmodule = {
                    http = {
                        exposedInterfaces = listOf(
                            "SomeInterface",
                            "SomeInterface2"
                        )
                        serverName = "\"someServerName\""
                        baseUrl = "\"someService.baseUrl\""
                        auth = "\"someService.auth\""
                        urlPathPrefix = "\"/some/prefix\""
                    }
                    playFabHandlers = {
                        exposedInterfaces = listOf(
                            {
                                name = "SomeInterface"
                                attributes = emptyList()
                            },
                            {
                                name = "SomeInterface2"
                                attributes = listOf {
                                    name = "debug"
                                }
                            }
                        )
                        errorCodesMapping = listOf(
                            {
                                exceptionName = "SomeException"
                                code = "\"EC1\""
                            },
                            {
                                exceptionName = "SomeException2"
                                code = "\"EC2\""
                            }
                        )
                    }
                }
            },
        ))
    }

    @Test
    fun `should parse view model submodule`() {
        val modules = parseSingleGroup("view-model-submodule")

        assertModules(modules, listOf {
            name = "SomeModule"
            viewModelSubmodule = {
                elements = listOf (
                    {
                        name = "SomeModelVm"
                        attributes = listOf(
                            {
                                name = "att1"
                            },
                            {
                                name = "att2"
                            }
                        )
                        model = {
                            name = "SomeModel"
                            mappedFields = listOf(
                                {
                                    name = "id"
                                },
                                {
                                    name = "field"
                                    mappedType = "OverriddenVm"
                                }
                            )
                        }
                    },
                    {
                        name = "SomeEmptyVm"
                        modelEmpty = true
                    }
                )
                windows = listOf {
                    name = "SomeWindow"
                    state = {
                        fields = listOf {
                            name = "name"
                            type = {
                                name = "string"
                            }
                        }
                    }
                    fields = listOf {
                        name = "someVm"
                        type = {
                            name = "SomeModelVm"
                        }
                    }
                }
                popups = listOf {
                    name = "SomePopup"
                    state = {
                        fields = listOf {
                            name = "name"
                            type = {
                                name = "string"
                            }
                        }
                    }
                    fields = listOf {
                        name = "someVm"
                        type = {
                            name = "SomeModelVm"
                        }
                    }
                }
            }
        })
    }

    @Test
    fun `should parse inlined simple vos`() {
        val module = parseSingleModule("inlined-vos")

        assertModuleDefinition(module) {
            simpleValueObjects = listOf(
                {
                    name = "SomeId"
                    typeName = "string"
                },
                {
                    name = "SomeId2"
                    typeName = "string"
                }
            )
            complexValueObjects = listOf {
                name = "SomeClass"
                fields = listOf(
                    {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    },
                    {
                        name = "optId2"
                        type = {
                            name = "SomeId2"
                            wrappers = listOf(
                                "OPTIONAL"
                            )
                        }
                    }
                )
            }
        }
    }

    @Test
    fun `should parse interface method with number in the name`() {
        val module = parseSingleModule("interface-method-with-number")

        assertModuleDefinition(module) {
            interfaces = listOf {
                name = "SomeInterface"
                methods = listOf {
                    name = "someMethod2"
                    args = listOf {
                        name = "someArg"
                        type = {
                            name = "int"
                        }
                    }
                }
            }
        }
    }
}