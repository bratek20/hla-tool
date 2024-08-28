package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.functionCall
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
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

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val moduleName = c.module.getName().value

        val allExposedInterfaces = c.module.getWebSubmodule()!!.getPlayFabHandlers()!!.getExposedInterfaces()
        val normalExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } == null}
        val debugExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } != null }

        addFunctionCall {
            name = "Handlers.Api.RegisterModuleHandlers"
            addArg(variable("DependencyName.$moduleName"))
            normalExposedInterfaces.forEach {
                module.getInterfaces().find { interf -> it.getName() == interf.getName() }?.let { interf ->
                    interf.getMethods().forEach { method ->
                        addArg(variable("[\"$moduleName.${it.getName()}.${method.getName()}\", ${method.getName()}]"))
                    }
                }
            }
        }

        addFunction {
            name = "RegisterDebugHandlers"
            body {
                addFunctionCall {
                    name = "Handlers.Api.RegisterModuleHandlers"
                    addArg(variable("DependencyName.$moduleName"))
                    debugExposedInterfaces.forEach {
                        module.getInterfaces().find { interf -> it.getName() == interf.getName() }?.let { interf ->
                            interf.getMethods().forEach { method ->
                                addArg(variable("[\"$moduleName.${it.getName()}.${method.getName()}\", ${method.getName()}]"))
                            }
                        }
                    }
                }
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
                                    addArg(variable("${it.getName()}Input"))
                                    addArg(variable("rawRequest"))
                                    addArg(variable("ObjectCreationOptions.noErrors()"))
                                }
                            }
                            addVariableAssignment {
                                declare = true
                                name = "response"
                                value = functionCall {
                                    name = "Api.${it.getName()}"
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

        addEmptyLines(5)
    }
}