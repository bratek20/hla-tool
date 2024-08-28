package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.functionCall
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.ops.variableAssignment
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
        addFunctionCall {
            name = "Handlers.Api.RegisterModuleHandlers"
            addArg(variable("DependencyName.SomeModule"))
            addArg(variable("[\"SomeModule.someHandler\", someHandler]"))
            addArg(variable("[\"SomeModule.someHandler2\", someHandler2]"))
        }

        addFunction {
            name = "RegisterDebugHandlers"
            body {
                addFunctionCall {
                    name = "Handlers.Api.RegisterModuleHandlers"
                    addArg(variable("DependencyName.SomeModule"))
                    addArg(variable("[\"SomeModule.Debug.someDebugHandler\", someDebugHandler]"))
                    addArg(variable("[\"SomeModule.Debug.someDebugHandler2\", someDebugHandler2]"))
                }
            }
        }

//        function someHandler(rawRequest: any, c: HandlerContext): IOpResult {
//            const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors());
//            const response = Api.someHandler(request, c);
//            return Utils.OK(response);
//        }
        addFunction {
            name = "someHandler"
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
                    blockValue = functionCall {
                        name = "ObjectCreation.Api.FromInterface"
                        addArg(variable("SomeHandlerInput"))
                        addArg(variable("rawRequest"))
                        addArg(variable("ObjectCreationOptions.noErrors()"))
                    }
                }
                addVariableAssignment {
                    name = "response"
                    blockValue = functionCall {
                        name = "Api.someHandler"
                        addArg(variable("request"))
                        addArg(variable("c"))
                    }
                }
            }
        }
        addEmptyLines(24)
    }
}