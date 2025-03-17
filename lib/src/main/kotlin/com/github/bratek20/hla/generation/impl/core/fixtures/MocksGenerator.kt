package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.TypeScriptNamespaceBuilder
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class MockInterfaceLogic(
    private val def: InterfaceDefinition,
    private val moduleName: String,
    private val apiTypeFactory: ApiTypeFactory
) {
    fun getClass(): ClassBuilderOps = {
        name = def.getName() + "Mock"
        implements = def.getName()

        def.getMethods().forEach { method ->
            addMethod {
                name = method.getName()
                method.getArgs().forEach { arg ->
                    addArg {
                        name = arg.getName()
                        type = apiTypeFactory.create(arg.getType()).builder()
                    }
                }
                returnType = apiTypeFactory.create(method.getReturnType()).builder()
            }
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
                                body = methodCall {
                                    target = variable("mock")
                                    methodName = method.getName()
                                    method.getArgs().forEach { arg ->
                                        addArg {
                                            variable(arg.getName())
                                        }
                                    }
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
        val mockInterfacesLogic = module.getInterfaces().map { MockInterfaceLogic(it, moduleName, apiTypeFactory) }

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