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
import com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
import com.github.bratek20.utils.camelToPascalCase
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
                    legacyComment(def.name)
                    legacyField {
                        modifier = AccessModifier.PRIVATE
                        name = callsListName
                        type = mutableListType(typeName(inputTypeName))
                        legacyValue = {
                            add(emptyMutableList(typeName(inputTypeName)))
                        }
                    }
                    legacyField {
                        modifier = AccessModifier.PRIVATE
                        name = responsesListName
                        type = mutableListType(pairType(typeName(expectedInputType), typeName(defOutputType)))
                        legacyValue = {
                            add(emptyMutableList(typeName(expectedInputType)))
                        }
                    }
                    emptyLine()

                    legacyMethod {
                        name = "set${upperCaseName}Response"
                        addArg {
                            name = "args"
                            type = typeName(expectedInputType)
                        }
                        addArg {
                            name = "response"
                            type = typeName(defOutputType)
                        }
                        setBody {
                            add(listOp(responsesListName).add {
                                newPair("args", "response")
                            })
                        }
                    }

                    emptyLine()
                    legacyMethod {
                        override = true
                        name = def.name
                        returnType = typeName(outputTypeName)
                        addArg {
                            name = inputArgName
                            type = typeName(inputTypeName)
                        }
                        setBody {
                            add(listOp(callsListName).add {
                                variable(inputArgName)
                            })
                            add(assignment {
                                declare = true
                                left = "findResult"

                                right = listOp(responsesListName).find {
                                    isEqualTo {
                                        left = functionCall {
                                            name = inputDiffMethodName
                                            addArg {
                                                variable(inputArgName)
                                            }
                                            addArg {
                                                pairOp("it").first()
                                            }
                                        }

                                        right = string("")
                                    }
                                }
                            })
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
                            type = typeName(expectedInputType)
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
                .addOps {
                    legacyClassBlock {
                        name = "${interfaceName}Mock"
                        implements = interfaceName
                        legacyBody = {
                            interf.methods.map {
                                addOps(mocksForMethod(it))
                            }
                        }
                    }
                }
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder(lang)
                .addOps {
                    legacyClassBlock {
                        name = "${moduleName}Mocks"
                        implements = "ContextModule"
                        legacyBody = {
                            legacyMethod {
                                override = true
                                name = "apply"
                                addArg {
                                    name = "builder"
                                    type = typeName("ContextBuilder")
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