package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.codebuilder.*
import com.github.bratek20.hla.codebuilder.Class
import com.github.bratek20.hla.codebuilder.Function
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.utils.camelToPascalCase

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    class View(
        val interf: InterfaceDefinition,
        val moduleName: String
    ) {
        private val interfaceName = interf.getName()

        private fun mocksForMethod(def: MethodDefinition): CodeBlockBuilder {
            val upperCaseName = camelToPascalCase(def.getName())
            return block {
                line("// ${def.getName()}")
                line(ListFieldDeclaration("${def.getName()}Calls", "OtherClass"))
                line(
                    ListFieldDeclaration(
                        "${def.getName()}Responses",
                        "Pair<ExpectedOtherClass.() -> Unit, OtherClassDef.() -> Unit>"
                    )
                )
                emptyLine()
                add(
                    Function(
                        name = "set${upperCaseName}Response",
                        args = listOf(
                            Pair("args", "ExpectedOtherClass.() -> Unit"),
                            Pair("response", "OtherClassDef.() -> Unit")
                        ),
                        body = OneLineBlock("${def.getName()}Responses.add(Pair(args, response))")
                    )
                )
                emptyLine()
                add(Function(
                    override = true,
                    name = "referenceOtherClass",
                    returnType = "OtherClass",
                    args = listOf(Pair("other", "OtherClass")),
                    body = block {
                        line("${def.getName()}Calls.add(other)")
                        line("return otherClass(${def.getName()}Responses.find { diffOtherClass(other, it.first) == \"\" }?.second ?: {})")
                    }
                ))
                emptyLine()
                add(Function(
                    name = "assert${upperCaseName}Called",
                    args = listOf(Pair("times", "Int = 1")),
                    body = block {
                        line("assertThat(${def.getName()}Calls.size).withFailMessage(\"Expected ${def.getName()} to be called \$times times, but was called \$${def.getName()}Calls times\").isEqualTo(times)")
                    }
                ))
                emptyLine()
                add(Function(
                    name = "assert${upperCaseName}CalledForArgs",
                    args = listOf(
                        Pair("args", "ExpectedOtherClass.() -> Unit"),
                        Pair("times", "Int = 1")
                    ),
                    body = block {
                        line("val calls = ${def.getName()}Calls.filter { diffOtherClass(it, args) == \"\" }")
                        line("assertThat(calls.size).withFailMessage(\"Expected ${def.getName()} to be called \$times times, but was called \$${def.getName()}Calls times\").isEqualTo(times)")
                    }
                ))
            }
        }

        fun classes(): String {
            return CodeBuilder()
                .add(Class(
                    className = "${interfaceName}Mock",
                    implementedInterfaceName = interfaceName,
                    body = ManyCodeBlocks(listOf(
                        mocksForMethod(interf.getMethods().find { it.getName() == "referenceOtherClass" }!!),
                        EmptyLineBlock(),
                        block {
                            line("// referenceLegacyType")
                            line(ListFieldDeclaration("referenceLegacyTypeCalls", "com.some.pkg.legacy.LegacyType"))
                            line(
                                ListFieldDeclaration(
                                    "referenceLegacyTypeResponses",
                                    "Pair<com.some.pkg.legacy.LegacyType, com.some.pkg.legacy.LegacyType>"
                                )
                            )
                            emptyLine()
                            add(
                                Function(
                                    name = "setReferenceLegacyTypeResponse",
                                    args = listOf(
                                        Pair("args", "com.some.pkg.legacy.LegacyType"),
                                        Pair("response", "com.some.pkg.legacy.LegacyType")
                                    ),
                                    body = OneLineBlock("referenceLegacyTypeResponses.add(Pair(args, response))")
                                )
                            )
                            emptyLine()
                            add(Function(
                                override = true,
                                name = "referenceLegacyType",
                                returnType = "com.some.pkg.legacy.LegacyType",
                                args = listOf(Pair("legacyType", "com.some.pkg.legacy.LegacyType")),
                                body = block {
                                    line("referenceLegacyTypeCalls.add(legacyType)")
                                    line("return referenceLegacyTypeResponses.find { it.first == legacyType }?.second ?: legacyType")
                                }
                            ))
                            emptyLine()
                            add(Function(
                                name = "assertReferenceLegacyTypeCalled",
                                args = listOf(Pair("times", "Int = 1")),
                                body = block {
                                    line("assertThat(referenceLegacyTypeCalls.size).withFailMessage(\"Expected referenceLegacyType to be called \$times times, but was called \$referenceLegacyTypeCalls times\").isEqualTo(times)")
                                }
                            ))
                            emptyLine()
                            add(Function(
                                name = "assertReferenceLegacyTypeCalledForArgs",
                                args = listOf(
                                    Pair("args", "com.some.pkg.legacy.LegacyType"),
                                    Pair("times", "Int = 1")
                                ),
                                body = block {
                                    line("val calls = referenceLegacyTypeCalls.filter { it == args }")
                                    line("assertThat(calls.size).withFailMessage(\"Expected referenceLegacyType to be called \$times times, but was called \$referenceLegacyTypeCalls times\").isEqualTo(times)")
                                }
                            ))
                        }
                    ))
                ))
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder()
                .add(Class(
                    className = "${moduleName}Mocks",
                    implementedInterfaceName = "ContextModule",
                    body = block {
                        add(Function(
                            override = true,
                            name = "apply",
                            args = listOf(Pair("builder", "ContextBuilder")),
                            body = block {
                                line("builder")
                                .tab()
                                .line(".setImpl($interfaceName::class.java, ${interfaceName}Mock::class.java)")
                                .untab()
                            }
                        ))
                    }
                ))
                .build()
        }
    }
    override fun generateFileContent(): FileContent? {
        if(c.module.getInterfaces().none { it.getName() == "SomeInterface2" }) {
            return null
        }
        val interf = c.module.getInterfaces().find { it.getName() == "SomeInterface2" }!!
        return contentBuilder("mocks.vm")
            .put("view", View(interf, "SomeModule"))
            .build()
    }
}