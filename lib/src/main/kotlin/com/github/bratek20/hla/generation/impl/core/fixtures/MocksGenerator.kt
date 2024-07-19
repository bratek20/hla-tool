package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderLanguage
import com.github.bratek20.codebuilder.core.linePartBlock
import com.github.bratek20.codebuilder.ops.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.hla.generation.impl.core.api.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.MethodView
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.directory.api.FileContent
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

        private fun mocksForMethod(def: MethodView): ClassBuilderOps {
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

            return {
                comment(def.name)
                field {
                    accessor = FieldAccessor.PRIVATE
                    name = callsListName
                    type = mutableListType(type(inputTypeName))
                    value = emptyMutableList()
                }
                field {
                    accessor = FieldAccessor.PRIVATE
                    name = responsesListName
                    type = mutableListType(pairType(type(expectedInputType), type(defOutputType)))
                    value = emptyMutableList()
                }
                emptyLine()

                method {
                    name = "set${upperCaseName}Response"
                    addArg {
                        name = "args"
                        type = type(expectedInputType)
                    }
                    addArg {
                        name = "response"
                        type = type(defOutputType)
                    }
                    body = {
                        listOp(responsesListName).add { newPair("args", "response") }
                    }
                }

                emptyLine()
                method {
                    override = true
                    name = def.name
                    returnType = type(outputTypeName)
                    addArg {
                        name = inputArgName
                        type = type(inputTypeName)
                    }
                    body = {
                        listOp(callsListName).add {
                            variable(inputArgName)
                        }
                        assign {
                            variable = "response"
                            value = {
                                listOp(responsesListName).find {
                                    val itName = it.name
                                    isEqualTo {
                                        left = {
                                            functionCall {
                                                name = inputDiffMethodName
                                                addArg { variable(inputArgName) }
                                                addArg { pairOp(itName).first() }
                                            }
                                        }
                                        right = { string("") }
                                    }
                                }
                            }
                        }
                        returnBlock {
                            //TODO support for ?., support for ?:
                            linePart("result?.second ?: $emptyDef)")
                        }
                    }
                }
                emptyLine()
                method {
                    name = "assert${upperCaseName}Called"
                    addArg {
                        name = "times"
                        type = baseType(BaseType.INT)
                        defaultValue = "1"
                    }
                    body = {
                        line("assertThat(${callsListName}.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                    }
                }
                emptyLine()
                method {
                    name = "assert${upperCaseName}CalledForArgs"
                    addArg {
                        name = "args"
                        type = type(expectedInputType)
                    }
                    addArg {
                        name = "times"
                        type = baseType(BaseType.INT)
                        defaultValue = "1"
                    }
                    body = {
                        line("val calls = ${callsListName}.filter { ${inputDiffMethodName}(it, args) == \"\" }")
                        line("assertThat(calls.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                    }
                }
            }
        }

        fun classes(indent: Int): String {
            return CodeBuilder(lang, indent)
                .add(classBlock {
                    name = "${interfaceName}Mock"
                    implementedInterfaceName = interfaceName
                    interf.methods.map {
                        this.apply(mocksForMethod(it))
                    }
                })
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder(lang)
                .add(classBlock {
                    name = "${moduleName}Mocks"
                    implementedInterfaceName = "ContextModule"
                    method {
                        override = true
                        name = "apply"
                        addArg {
                            name = "builder"
                            type = type("ContextBuilder")
                        }
                        body = {
                            line("builder")
                            tab()
                            line(".setImpl($interfaceName::class.java, ${interfaceName}Mock::class.java)")
                            untab()
                        }
                    }
                })
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