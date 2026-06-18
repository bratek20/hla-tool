package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.TypeScriptStructureBuilder
import com.github.bratek20.codebuilder.languages.typescript.typeScriptStructure
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.newListOf
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.definitions.api.ExposedInterface
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.definitions.api.HandlerNameMapping
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.PlayFabHandlersDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.queries.api.isDebug

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
        exposedInterfaces: List<ExposedInterface>
    ): FunctionCallBuilderOps = {
        val logic = PlayFabHandlerLogic(module, c.module.getWebSubmodule()!!.getPlayFabHandlers()!!)
        val handlers: List<TypeScriptStructureBuilder> = logic.getHandlers(exposedInterfaces)

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

    private fun getPlayFabHandlers() = c.module.getWebSubmodule()!!.getPlayFabHandlers()!!

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val playFabHandlers = getPlayFabHandlers()
        val allExposedInterfaces = playFabHandlers.getExposedInterfaces()
        val normalExposedInterfaces = allExposedInterfaces.filter { !it.isDebug() }
        val debugExposedInterfaces = allExposedInterfaces.filter { it.isDebug() }

        addFunctionCall(registerCall(normalExposedInterfaces))

        if (debugExposedInterfaces.isNotEmpty()) {
            addFunction {
                name = "RegisterDebugHandlers"
                setBody {
                    add(functionCallStatement(registerCall(debugExposedInterfaces)))
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

class PlayFabHandlerLogic (
    private val module: ModuleDefinition,
    private val playFabHandlersDefinition: PlayFabHandlersDefinition? = null
){
    fun handlerName(
        method: MethodDefinition,
        exposedInterface: ExposedInterface,
        interfaceDefinition: InterfaceDefinition
    ): String {
        val mapping = playFabHandlersDefinition?.getHandlerNamesMapping()?.firstOrNull {
            it.getMethodPath() == "${interfaceDefinition.getName()}.${method.getName()}"
        }
        if (mapping != null) {
            return mapping.getHandlerName().replace("\"","")
        }

        val debugPart = if (exposedInterface.isDebug()) ".Debug" else ""
        return "${module.getName()}$debugPart.${method.getName()}"
    }

    fun getHandlers(exposedInterfaces: List<ExposedInterface>) : List<TypeScriptStructureBuilder> {
        return exposedInterfaces.flatMap {
            module.getInterfaces().first { interf -> it.getName() == interf.getName() }.let { interf ->
                interf.getMethods().map { method ->
                    typeScriptStructure {
                        addProperty {
                            key = "name"
                            value = string(
                                handlerName(method, it, interf)
                            )
                        }
                        addProperty {
                            key = "handler"
                            value = variable(method.getName())
                        }
                    }
                }
            }
        }
    }
}