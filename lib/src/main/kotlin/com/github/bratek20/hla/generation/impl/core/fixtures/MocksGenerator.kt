package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

class MocksGenerator: PatternGenerator() {
    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getInterfaces().isNotEmpty()
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }

    class View(
        val c: ModuleGenerationContext,
        val lang: CodeBuilderLanguage,
        val interf: InterfaceView,
        val moduleName: String
    ) {
        private val interfaceName = interf.name

        private fun mocksForMethod(def: MethodView): CodeBuilderOps {
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
                        value = {
                            add(emptyMutableList(type(inputTypeName)))
                        }
                    }
                    field {
                        accessor = FieldAccessor.PRIVATE
                        name = responsesListName
                        type = mutableListType(pairType(type(expectedInputType), type(defOutputType)))
                        value = {
                            add(emptyMutableList(type(expectedInputType)))
                        }
                    }
                    emptyLine()

                    legacyMethod {
                        name = "set${upperCaseName}Response"
                        addArg {
                            name = "args"
                            type = type(expectedInputType)
                        }
                        addArg {
                            name = "response"
                            type = type(defOutputType)
                        }
                        legacyBody = {
                            listOp(responsesListName).add { newPair("args", "response") }
                        }
                    }

                    emptyLine()
                    legacyMethod {
                        override = true
                        name = def.name
                        returnType = type(outputTypeName)
                        addArg {
                            name = inputArgName
                            type = type(inputTypeName)
                        }
                        legacyBody = {
                            listOp(callsListName).add {
                                legacyVariable(inputArgName)
                            }
                            legacyAssign {
                                variable = {
                                    declare = true
                                    name = "findResult"
                                }
                                value = {
                                    listOp(responsesListName).find {
                                        isEqualTo {
                                            left = {
                                                legacyFunctionCall {
                                                    name = inputDiffMethodName
                                                    addArgLegacy { legacyVariable(inputArgName) }
                                                    addArgLegacy { pairOp(it.name).first() }
                                                }
                                            }
                                            right = { string("") }
                                        }
                                    }
                                }
                            }
                            legacyReturn {
                                //TODO support for ?., support for ?:
                                linePart("$outputBuilderMethodName(findResult?.second ?: $emptyDef)")
                            }
                        }
                    }
                    emptyLine()
                    legacyMethod {
                        name = "assert${upperCaseName}Called"
                        addArg {
                            name = "times"
                            type = baseType(BaseType.INT)
                            defaultValue = "1"
                        }
                        legacyBody = {
                            line("assertThat(${callsListName}.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                        }
                    }
                    emptyLine()
                    legacyMethod {
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
                        legacyBody = {
                            line("val calls = ${callsListName}.filter { ${inputDiffMethodName}(it, args) == \"\" }")
                            line("assertThat(calls.size).withFailMessage(\"Expected ${def.name} to be called \$times times, but was called \$${def.name}Calls times\").isEqualTo(times)")
                        }
                    }
            }
        }

        fun classes(indent: Int): String {
            return CodeBuilder(lang, indent)
                .add {
                    classBlock {
                        name = "${interfaceName}Mock"
                        implements = interfaceName
                        body = {
                            interf.methods.map {
                                add(mocksForMethod(it))
                            }
                        }
                    }
                }
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder(lang)
                .add {
                    classBlock {
                        name = "${moduleName}Mocks"
                        implements = "ContextModule"
                        body = {
                            legacyMethod {
                                override = true
                                name = "apply"
                                addArg {
                                    name = "builder"
                                    type = type("ContextBuilder")
                                }
                                legacyBody = {
                                    line("builder")
                                    tab()
                                    line(".setImpl($interfaceName::class.java, ${interfaceName}Mock::class.java)")
                                    untab()
                                }
                            }
                        }
                    }
                }
                .build()
        }
    }
}