package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.languages.kotlin.profileToRootPackage
import com.github.bratek20.utils.camelToPascalCase

class MockInterfaceLogic(
    private val def: InterfaceDefinition,
    private val moduleName: String,
    private val apiTypeFactory: ApiTypeFactory,
    private val defTypeFactory: DefTypeFactory,
    private val languageName: ModuleLanguage
) {
    fun mockClass(): ClassBuilderOps = {
        name = def.getName() + "Mock"
        implements = def.getName()

        def.getMethods().forEach { method ->
            addField(callsField(method))

            addMethod(mockedMethod(method))
            addMethod(callsAssertion(method))

            if (!hasVoidReturnType(method) && !methodReturnExternalApiType(method)) {
                addField(responseField(method))
                addMethod(setResponse(method))
            }
        }

        addMethod(resetMethod())
    }

    private fun methodReturnExternalApiType(method: MethodDefinition): Boolean {
        val returnType = returnApiType(method)
        return returnType is ExternalApiType
    }

    private fun callsField(method: MethodDefinition): FieldBuilderOps {
        return {
            type = baseType(BaseType.INT)
            name = callsVariableName(method)
            value = const("0")
            mutable = true
        }
    }

    private fun responseField(method: MethodDefinition): FieldBuilderOps {
        val returnType = returnApiType(method)

        val emptyValue = if (returnType is ListApiType) {
            emptyImmutableList(returnType.wrappedType.builder())
        } else if(returnType is OptionalApiType) {
            nullValue()
        }else if(returnType is SerializableApiType || BaseApiType.isAny(returnType)) {
           if(isKotlin()) emptyLambda() else nullValue()
        }else {
            nullValue()
        }

        return {
            type = returnDefType(method).builder()
            name = method.getName() + "Response"
            value = emptyValue
            mutable = true
        }
    }

    private fun isKotlin(): Boolean {
        return languageName == ModuleLanguage.KOTLIN
    }

    private fun setResponse(method: MethodDefinition): MethodBuilderOps = {
        name = "set${camelToPascalCase(method.getName())}Response"
        addArg {
            name = "response"
            type = returnDefType(method).builder()
        }
        setBody {
            add(assignment {
                left = instanceVariable(method.getName() + "Response")
                right = variable("response")
            })
        }
    }

    private fun returnApiType(method: MethodDefinition): ApiType {
        return apiTypeFactory.create(method.getReturnType())
    }

    private fun returnDefType(method: MethodDefinition): DefType<*> {
        return defTypeFactory.create(returnApiType(method) as ApiTypeLogic)
    }

    private fun mockedMethod(method: MethodDefinition): MethodBuilderOps = {
        name = method.getName()
        this.overridesClassMethod = isKotlin()

        method.getArgs().forEach { arg ->
            addArg {
                name = arg.getName()
                type = apiTypeFactory.create(arg.getType()).builder()
            }
        }
        val returnType = returnApiType(method)
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
                    defaultBuilderCall(returnType, method)
                })
            }
        }
    }

    private fun callsAssertion(method: MethodDefinition): MethodBuilderOps = {
        name = "assert${camelToPascalCase(method.getName())}Calls"
        val expectedValue = "expectedNumber"
        val expectedValueExpression = variable (expectedValue)
        val givenExpression = instanceVariable(callsVariableName(method))
        addArg {
            name = expectedValue
            type = baseType(BaseType.INT)
        }
        setBody {
            add(assertEquals {
                given = givenExpression
                expected = expectedValueExpression
                message = plus {
                    left = string("Expected '${method.getName()}' to be called ")
                    right = plus {
                        left = variable(expectedValue)
                        right = plus {
                            left = string(" times but was called ")
                            right = plus {
                                left = givenExpression
                                right = string(" times")
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

    private fun defaultBuilderCall(type: ApiType, method: MethodDefinition): ExpressionBuilder {
        return defTypeFactory.create(type as ApiTypeLogic).modernBuild(instanceVariable(method.getName() + "Response"))
    }

    fun createMock(): FunctionBuilderOps = {
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

    fun setup(): FunctionBuilderOps = {
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

    override fun extraKotlinImports(): List<String> {
        val moduleName = module.getName().value.lowercase()
        val modulePackage = profileToRootPackage(moduleGroup.getProfile())

        return listOf(
            "$modulePackage.$moduleName.api.*",
            "org.assertj.core.api.Assertions.assertThat"
        )
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return getMockedInterfaces().isNotEmpty() && language.name() != ModuleLanguage.C_SHARP
    }

    private fun getMockedInterfaces(): List<InterfaceDefinition> {
        return (module.getFixturesSubmodule()?.getMockedInterfaces() ?: emptyList())
            .map { module.getInterfaces().first { i -> i.getName() == it } }
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val mockInterfacesLogic = getMockedInterfaces().map { MockInterfaceLogic(it, moduleName, apiTypeFactory, DefTypeFactory(language.buildersFixture()), language.name()) }

        mockInterfacesLogic.forEach { logic ->
            addClass(logic.mockClass())
        }

        if(language.name() == ModuleLanguage.TYPE_SCRIPT) {
            add(typeScriptNamespace {
                name = "$moduleName.Mocks"
                mockInterfacesLogic.forEach { logic ->
                    addFunction(logic.createMock())
                    addFunction(logic.setup())
                }
            })
        }
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}