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

class MockMethodLogic(
    private val def: MethodDefinition,
    private val apiTypeFactory: ApiTypeFactory,
    private val defTypeFactory: DefTypeFactory,
    private val languageName: ModuleLanguage
) {
    fun getResetAssignments(): List<AssignmentBuilder> {
        return listOf(
            assignment {
                left = instanceVariable(callsNumberVariableName())
                right = const("0")
            }
        )
    }

    fun callsNumberVariableName(): String {
        return def.getName() + "CallsNumber"
    }

    fun callsNumberField(): FieldBuilderOps {
        return {
            type = baseType(BaseType.INT)
            name = callsNumberVariableName()
            value = const("0")
            mutable = true
        }
    }

    fun setResponse(): MethodBuilderOps = {
        name = "set${camelToPascalCase(def.getName())}Response"
        addArg {
            name = "response"
            type = returnDefType().builder()
        }
        setBody {
            add(assignment {
                left = instanceVariable(def.getName() + "Response")
                right = variable("response")
            })
        }
    }

    private fun returnApiType(): ApiType {
        return apiTypeFactory.create(def.getReturnType())
    }

    private fun returnDefType(): DefType<*> {
        return defTypeFactory.create(returnApiType() as ApiTypeLogic)
    }

    fun mockedMethod(): MethodBuilderOps = {
        name = def.getName()
        this.overridesClassMethod = isKotlin()

        def.getArgs().forEach { arg ->
            addArg {
                name = arg.getName()
                type = apiTypeFactory.create(arg.getType()).builder()
            }
        }
        val returnType = returnApiType()
        this.returnType = returnType.builder()

        setBody {
            add(assignment {
                left = instanceVariable(callsNumberVariableName())
                right = plus {
                    left = instanceVariable(callsNumberVariableName())
                    right = const("1")
                }
            })
            if (!hasVoidReturnType()) {
                add(returnStatement {
                    defaultBuilderCall(returnType)
                })
            }
        }
    }

    fun callsNumberAssertion(): MethodBuilderOps = {
        name = "assert${camelToPascalCase(def.getName())}CallsNumber"
        val expectedValue = "expectedNumber"
        val expectedValueExpression = variable (expectedValue)
        val givenExpression = instanceVariable(callsNumberVariableName())
        addArg {
            name = expectedValue
            type = baseType(BaseType.INT)
        }
        setBody {
            add(assertEquals {
                given = givenExpression
                expected = expectedValueExpression
                message = plus {
                    left = string("Expected '${def.getName()}' to be called ")
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

    fun hasVoidReturnType(): Boolean {
        return BaseApiType.isVoid(apiTypeFactory.create(def.getReturnType()))
    }

    private fun isKotlin(): Boolean {
        return languageName == ModuleLanguage.KOTLIN
    }

    fun methodReturnExternalApiType(): Boolean {
        val returnType = returnApiType()
        return returnType is ExternalApiType
    }

    private fun defaultBuilderCall(type: ApiType): ExpressionBuilder {
        return defTypeFactory.create(type).modernBuild(instanceVariable(def.getName() + "Response"))
    }

    fun responseField(): FieldBuilderOps {
        val returnType = returnApiType()
        val returnDefType = defTypeFactory.create(returnType)

        return {
            type = returnDefType().builder()
            name = def.getName() + "Response"
            value = returnDefType.emptyValueBuilder()
            mutable = true
        }
    }

    fun setupAssignment(moduleName: String): AssignmentBuilder {
        return assignment {
            val apiMethodName = "$moduleName.Api.${def.getName()}"

            left = variable(apiMethodName)
            right = functionCall {
                name = "CreateMock"
                addArg {
                    variable(apiMethodName)
                }
                addArg {
                    lambda {
                        def.getArgs().forEach {
                            addArg {
                                name = it.getName()
                                type = apiTypeFactory.create(it.getType()).builder()
                            }
                        }
                        val mockMethodCall = methodCall {
                            target = variable("mock")
                            methodName = def.getName()
                            def.getArgs().forEach { arg ->
                                addArg {
                                    variable(arg.getName())
                                }
                            }
                        }
                        body = if (hasVoidReturnType()) {
                            mockMethodCall
                        } else {
                            returnExpression(mockMethodCall)
                        }
                    }
                }
            }
        }
    }
}

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

        getMethodsLogic().forEach { method ->
            addField(method.callsNumberField())

            addMethod(method.mockedMethod())
            addMethod(method.callsNumberAssertion())

            if (!method.hasVoidReturnType() && !method.methodReturnExternalApiType()) {
                addField(method.responseField())
                addMethod(method.setResponse())
            }
        }

        addMethod(resetMethod())
    }

    private fun getMethodsLogic(): List<MockMethodLogic> {
        return def.getMethods().map { methodDef ->
            createMethodLogic(methodDef)
        }
    }

    private fun createMethodLogic(def: MethodDefinition): MockMethodLogic {
        return MockMethodLogic(def, apiTypeFactory, defTypeFactory, languageName)
    }

    private fun resetMethod(): MethodBuilderOps = {
        name = "reset"
        setBody {
            addMany(getMethodsLogic().flatMap { it.getResetAssignments() })
        }
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

            getMethodsLogic().forEach { method ->
                add(method.setupAssignment(moduleName))
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
        return modules.getMockedInterfaces(module)
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