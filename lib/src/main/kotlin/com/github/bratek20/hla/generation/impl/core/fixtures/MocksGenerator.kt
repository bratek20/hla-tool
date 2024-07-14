package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.*
import com.github.bratek20.codebuilder.builders.method
import com.github.bratek20.codebuilder.types.ListFieldDeclaration
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.hla.generation.impl.core.api.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.MethodView
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.pascalToCamelCase

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    class View(
        val c: ModuleGenerationContext,
        val lang: CodeBuilderLanguage,
        val interf: InterfaceView,
        val moduleName: String
    ) {
        private val interfaceName = interf.name

        private fun mocksForMethod(def: MethodView): CodeBlockBuilder {
            val upperCaseName = camelToPascalCase(def.name)
            val inputArgName = def.args.first().name
            val inputType = def.args.first().apiType
            val inputTypeName = def.args.first().type
            val outputType = def.returnApiType
            val outputTypeName = def.returnType!!

            val expectedInputType = if (inputType is ExternalApiType)
                    inputTypeName
                else
                    ExpectedTypeFactory(c).create(inputType).name()

            val defOutputType = if (outputType is ExternalApiType)
                    outputTypeName
                else
                    DefTypeFactory(c.language.buildersFixture()).create(outputType).name()

            val inputDiffMethodName = if (inputType is ExternalApiType)
                    "diff${inputType.rawName}"
                else
                    "diff${inputTypeName}"

            val outputBuilderMethodName = if (outputType is ExternalApiType)
                    pascalToCamelCase(outputType.rawName)
                else
                    pascalToCamelCase(outputTypeName)

            val callsListName = "${def.name}Calls"
            val responsesListName = "${def.name}Responses"


            val emptyDef = if (outputType is ExternalApiType)
                    "null"
                else
                    "{}"

            return block {
                line("// ${def.name}")
                add(
                    ListFieldDeclaration(
                        callsListName,
                        inputTypeName
                    )
                )
                add(
                    ListFieldDeclaration(
                        fieldName = responsesListName,
                        fieldElementType = "Pair<${expectedInputType}, ${defOutputType}>"
                    )
                )
                emptyLine()
                add(
                    method {
                        name = "set${upperCaseName}Response"
                        args = listOf(
                            Pair("args", expectedInputType),
                            Pair("response", defOutputType)
                        )
                        body = OneLineBlock("${responsesListName}.add(Pair(args, response))")
                    }
                )
                emptyLine()
                add(method {
                    override = true
                    name = def.name
                    returnType = outputTypeName
                    args = listOf(Pair(inputArgName, inputTypeName))
                    body = block {
                        line("${callsListName}.add($inputArgName)")
                        line("return ${outputBuilderMethodName}(${responsesListName}.find { ${inputDiffMethodName}(${inputArgName}, it.first) == \"\" }?.second ?: $emptyDef)")
                    }
                })
                emptyLine()
                add(method {
                    name = "assert${upperCaseName}Called"
                    args = listOf(Pair("times", "Int = 1"))
                    body = block {
                        line("assertThat(${callsListName}.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                    }
                })
                emptyLine()
                add(method {
                    name = "assert${upperCaseName}CalledForArgs"
                    args = listOf(
                        Pair("args", expectedInputType),
                        Pair("times", "Int = 1")
                    )
                    body = block {
                        line("val calls = ${callsListName}.filter { ${inputDiffMethodName}(it, args) == \"\" }")
                        line("assertThat(calls.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                    }
                })
            }
        }

        fun classes(indent: Int): String {
            return CodeBuilder(lang, indent)
                .addClass {
                    name = "${interfaceName}Mock"
                    implementedInterfaceName = interfaceName
                    body = ManyCodeBlocksSeparatedByLine(interf.methods.map {
                        mocksForMethod(it)
                    })
                }
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder(lang)
                .addClass {
                    name = "${moduleName}Mocks"
                    implementedInterfaceName = "ContextModule"
                    body = block {
                        add(method {
                            override = true
                            name = "apply"
                            args = listOf("builder" to "ContextBuilder")
                            body = block {
                                line("builder")
                                    .tab()
                                    .line(".setImpl($interfaceName::class.java, ${interfaceName}Mock::class.java)")
                                    .untab()
                            }
                        })
                    }
                }
                .build()
        }
    }
    override fun generateFileContent(): FileContent? {
        if(c.module.getInterfaces().none { it.getName() == "SomeInterface2" }) {
            return null
        }
        val interf = c.module.getInterfaces().find { it.getName() == "SomeInterface2" }!!
        val interfView = InterfaceViewFactory(apiTypeFactory).create(interf)
        return contentBuilder("mocks.vm")
            .put("view", View(c, lang, interfView, "SomeModule"))
            .build()
    }
}