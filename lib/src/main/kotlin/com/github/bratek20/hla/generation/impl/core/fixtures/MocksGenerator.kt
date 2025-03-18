package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.ApiTypeLogic
import com.github.bratek20.hla.apitypes.impl.BaseApiType
import com.github.bratek20.hla.apitypes.impl.ListApiType
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.camelToPascalCase

class MockInterfaceLogic(
    private val def: InterfaceDefinition,
    private val moduleName: String,
    private val apiTypeFactory: ApiTypeFactory,
    private val defTypeFactory: DefTypeFactory
) {
    fun getClass(): ClassBuilderOps = {
        name = def.getName() + "Mock"
        implements = def.getName()

        def.getMethods().forEach { method ->
            addField {
                type = baseType(BaseType.INT)
                name = callsVariableName(method)
                value = const("0")
            }

            addMethod(mockedMethod(method))
            addMethod(callsAssertion(method))
            if (!hasVoidReturnType(method)) {
                val x = defTypeFactory.create(apiTypeFactory.create(method.getReturnType()) as ApiTypeLogic).builder()
                addField {
                    type = x
                    name = method.getName() + "Response"
                }
                addMethod {
                    name = "set${camelToPascalCase(method.getName())}Response"
                    addArg {
                        name = "response"
                        type = x
                    }
                    setBody {
                        add(assignment {
                            left = instanceVariable(method.getName() + "Response")
                            right = variable("response")
                        })
                    }
                }
            }
        }

        addMethod(resetMethod())
    }

    private fun mockedMethod(method: MethodDefinition): MethodBuilderOps = {
        name = method.getName()
        method.getArgs().forEach { arg ->
            addArg {
                name = arg.getName()
                type = apiTypeFactory.create(arg.getType()).builder()
            }
        }
        val returnType = apiTypeFactory.create(method.getReturnType())
        this.returnType = returnType.builder()

        setBody {
            add(assignment {
                left = instanceVariable(callsVariableName(method))
                right = plus {
                    left = instanceVariable(callsVariableName(method))
                    right = const("1")
                }
            })
            if (!hasVoidReturnType(method)) {
                add(returnStatement {
                    defaultBuilderCall(returnType)
                })
            }
        }
    }

    private fun callsAssertion(method: MethodDefinition): MethodBuilderOps = {
        name = "assert${camelToPascalCase(method.getName())}Calls"
        addArg {
            name = "expectedNumber"
            type = baseType(BaseType.INT)
        }
        setBody {
            add(functionCallStatement {
                name = "AssertEquals"
                addArg {
                    instanceVariable(callsVariableName(method))
                }
                addArg {
                    variable("expectedNumber")
                }
                addArg {
                    plus {
                        left = string("Expected '${method.getName()}' to be called ")
                        right = plus {
                            left = variable("expectedNumber")
                            right = plus {
                                left = string(" times but was called ")
                                right = plus {
                                    left = instanceVariable(callsVariableName(method))
                                    right = string(" times")
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun resetMethod(): MethodBuilderOps = {
        name = "reset"
        setBody {
            def.getMethods().forEach { method ->
                add(assignment {
                    left = instanceVariable(callsVariableName(method))
                    right = const("0")
                })
            }
        }
    }

    private fun callsVariableName(method: MethodDefinition): String {
        return method.getName() + "Calls"
    }

    private fun hasVoidReturnType(method: MethodDefinition): Boolean {
        return BaseApiType.isVoid(apiTypeFactory.create(method.getReturnType()))
    }

    private fun defaultBuilderCall(type: ApiType): ExpressionBuilder {
        try {
            val emptyValue = if (type is ListApiType) {
                emptyImmutableList(type.wrappedType.builder())
            } else {
                nullValue()
            }
            return defTypeFactory.create(type as ApiTypeLogic).modernBuild(emptyValue)
        }
        catch (e: Exception) {
            return hardcodedExpression("TODO()")
        }
    }

    fun getCreateMockFunction(): FunctionBuilderOps = {
        name = "create${def.getName()}Mock"
        returnType = typeName(def.getName() + "Mock")
        setBody {
            add(returnStatement {
                constructorCall {
                    className = def.getName() + "Mock"
                }
            })
        }
    }

    fun getSetupFunction(): FunctionBuilderOps = {
        name = "setup${def.getName()}"
        returnType = typeName(def.getName() + "Mock")
        setBody {
            add(assignment {
                left = variableDeclaration {
                    name = "mock"
                }
                right = functionCall {
                    name = "$moduleName.Mocks.create${def.getName()}Mock"
                }
            })

            def.getMethods().forEach { method ->
                add(assignment {
                    val apiMethodName = "$moduleName.Api.${method.getName()}"

                    left = variable(apiMethodName)
                    right = functionCall {
                        name = "CreateMock"
                        addArg {
                            variable(apiMethodName)
                        }
                        addArg {
                            lambda {
                                method.getArgs().forEach {
                                    addArg {
                                        name = it.getName()
                                        type = apiTypeFactory.create(it.getType()).builder()
                                    }
                                }
                                val mockMethodCall = methodCall {
                                    target = variable("mock")
                                    methodName = method.getName()
                                    method.getArgs().forEach { arg ->
                                        addArg {
                                            variable(arg.getName())
                                        }
                                    }
                                }
                                body = if (hasVoidReturnType(method)) {
                                    mockMethodCall
                                } else {
                                    returnExpression(mockMethodCall)
                                }
                            }
                        }
                    }
                })
            }

            add(returnStatement {
                variable("mock")
            })
        }
    }
}
class MocksGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getInterfaces().isNotEmpty() && language.name() == ModuleLanguage.TYPE_SCRIPT
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val mockInterfacesLogic = module.getInterfaces().map { MockInterfaceLogic(it, moduleName, apiTypeFactory, DefTypeFactory(language.buildersFixture())) }

        mockInterfacesLogic.forEach { logic ->
            addClass(logic.getClass())
        }

        add(typeScriptNamespace {
            name = "$moduleName.Mocks"
            mockInterfacesLogic.forEach { logic ->
                addFunction(logic.getCreateMockFunction())
                addFunction(logic.getSetupFunction())
            }
        })
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}