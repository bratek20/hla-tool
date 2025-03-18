package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.TypeScriptStructureBuilder
import com.github.bratek20.codebuilder.languages.typescript.typeScriptStructure
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.newListOf
import com.github.bratek20.codebuilder.types.typeName
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

    private fun registerCall(
        exposedInterfaces: List<ExposedInterface>,
        addDebugToHandlerName: Boolean = false
    ): FunctionCallBuilderOps = {
        val handlers: List<TypeScriptStructureBuilder> = exposedInterfaces.flatMap {
            module.getInterfaces().first { interf -> it.getName() == interf.getName() }.let { interf ->
                interf.getMethods().map { method ->
                    val debugPart = if (addDebugToHandlerName) ".Debug" else ""
                    typeScriptStructure {
                        addProperty {
                            key = "name"
                            value = string("$moduleName$debugPart.${method.getName()}")
                        }
                        addProperty {
                            key = "handler"
                            value = variable(method.getName())
                        }
                    }
                }
            }
        }

        val behindFeatureFlag = exposedInterfaces.any { it.getAttributes().any { att -> att.getName() == "behindFeatureFlag" } }
        name = "Handlers.Api.Register"
        addArg {
            typeScriptStructure {
                addProperty {
                    key = "dependencyName"
                    value = variable("DependencyName.$moduleName")
                }

                addProperty {
                    key = "handlers"
                    value = newListOf(baseType(BaseType.ANY), *handlers.toTypedArray())
                }

                if (behindFeatureFlag) {
                    addProperty {
                        key = "featureFlag"
                        value = variable("FeatureName.$moduleName")
                    }
                }
            }
        }
    }
    override fun getOperations(): TopLevelCodeBuilderOps = {
        val playFabHandlers = c.module.getWebSubmodule()!!.getPlayFabHandlers()!!
        val allExposedInterfaces = playFabHandlers.getExposedInterfaces()
        val normalExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } == null}
        val debugExposedInterfaces = allExposedInterfaces.filter { it.getAttributes().find { it.getName() == "debug" } != null }

        addFunctionCall(registerCall(normalExposedInterfaces))

        if (debugExposedInterfaces.isNotEmpty()) {
            addFunction {
                name = "RegisterDebugHandlers"
                setBody {
                    add(functionCallStatement(registerCall(debugExposedInterfaces, true)))
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
                            type = typeName("HandlerContext")
                        }
                        returnType = typeName("IOpResult")

                        setBody {
                            require(method.getArgs().size <= 1) {
                                "Handler method need to have at most one argument"
                            }
                            val hasRequest = method.getArgs().size == 1
                            if (hasRequest) {
                                add(assignment {
                                    left = variableDeclaration {
                                        name = "request"
                                    }
                                    right = functionCall {
                                        name = "ObjectCreation.Api.FromInterface"
                                        addArg{
                                            variable(method.getArgs().first().getType().getName())
                                        }
                                        addArg{
                                            variable("rawRequest")
                                        }
                                        addArg{
                                            variable("ObjectCreationOptions.noErrors()")
                                        }
                                    }
                                })
                            }

                            val hasResponse = method.getReturnType().getName() != "void"
                            val apiCall: FunctionCallBuilderOps = {
                                name = "Api." + method.getName()
                                if (hasRequest) {
                                    addArg{
                                        variable("request")
                                    }
                                }
                                addArg{
                                    variable("c")
                                }
                            }

                            if (hasResponse) {
                                add(assignment {
                                    left = variableDeclaration {
                                        name = "response"
                                    }
                                    right = functionCall(apiCall)
                                })
                            }
                            else {
                                add(functionCallStatement(apiCall))
                            }

                            add(returnStatement {
                                functionCall {
                                    name = "Utils.OK"
                                    if (hasResponse) {
                                        addArg{
                                            variable("response")
                                        }
                                    } else {
                                        addArg{
                                            expression("{}")
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }

        if (playFabHandlers.getErrorCodesMapping().isNotEmpty()) {
            addComment {
                "Error Codes Mapping"
            }
            playFabHandlers.getErrorCodesMapping().forEach {
                addFunctionCall {
                    name = "Handlers.Api.AddExceptionMapper"
                    addArg{
                        variable(it.getExceptionName())
                    }
                    addArg{
                        expression("(e, c) => Utils.ECNR(${it.getCode()}, e.message, c)")
                    }
                }
            }
        }
    }
}