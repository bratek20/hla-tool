package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.hla.definitions.api.ExposedInterface
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class PlayFabHandlersGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PlayFabHandlers
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT && c.module.getWebSubmodule()?.getPlayFabHandlers() != null
    }

    private fun registerModuleHandlersCall(
        exposedInterfaces: List<ExposedInterface>,
        addDebugToHandlerName: Boolean = false
    ): FunctionCallBuilderOps = {
        name = "Handlers.Api.RegisterModuleHandlers"
        addArg(variable("DependencyName.$moduleName"))
        exposedInterfaces.forEach {
            module.getInterfaces().find { interf -> it.getName() == interf.getName() }?.let { interf ->
                interf.getMethods().forEach { method ->
                    val debugPart = if (addDebugToHandlerName) ".Debug" else ""
                    addArg(variable("[\"$moduleName$debugPart.${method.getName()}\", ${method.getName()}]"))
                }
            }
        }
    }
    override fun getOperations(): TopLevelCodeBuilderOps = {
        val playFabHandlers = c.module.getWebSubmodule()!!.getPlayFabHandlers()!!
        val allExposedInterfaces = playFabHandlers.getExposedInterfaces()
        val normalExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } == null}
        val debugExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } != null }

        addFunctionCall(registerModuleHandlersCall(normalExposedInterfaces))

        addFunction {
            name = "RegisterDebugHandlers"
            body {
                addFunctionCall(registerModuleHandlersCall(debugExposedInterfaces, true))
            }
        }

        allExposedInterfaces.forEach {
            module.getInterfaces().find { interf -> it.getName() == interf.getName() }?.let { interf ->
                interf.getMethods().forEach { method ->
                    addFunction {
                        name = method.getName()
                        addArg {
                            name = "rawRequest"
                            type = baseType(BaseType.ANY)
                        }
                        addArg {
                            name = "c"
                            type = type("HandlerContext")
                        }
                        returnType = type("IOpResult")

                        body {
                            addVariableAssignment {
                                declare = true
                                name = "request"
                                value = functionCall {
                                    name = "ObjectCreation.Api.FromInterface"

                                    require(method.getArgs().size == 1)
                                    addArg(variable(method.getArgs()[0].getType().getName()))
                                    addArg(variable("rawRequest"))
                                    addArg(variable("ObjectCreationOptions.noErrors()"))
                                }
                            }
                            addVariableAssignment {
                                declare = true
                                name = "response"
                                value = functionCall {
                                    name = "Api.${method.getName()}"
                                    addArg(variable("request"))
                                    addArg(variable("c"))
                                }
                            }
                            addReturn(functionCall {
                                name = "Utils.OK"
                                addArg(variable("response"))
                            })
                        }
                    }
                }
            }
        }

        addComment("Error Codes Mapping")
        playFabHandlers.getErrorCodesMapping().forEach {
            addFunctionCall {
                name = "Handlers.Api.AddExceptionMapper"
                addArg(variable(it.getExceptionName()))
                addArg(expression("(e, c) => Utils.ECNR(${it.getCode()}, e.message, c)"))
            }
        }
    }
}