package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ViewModelSharedLogic

class ViewModelContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ViewModel
    }

    override fun supportsCodeBuilder(): Boolean {
        return language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getViewModelSubmodule() != null
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val logic = ViewModelSharedLogic(module, apiTypeFactory, typesWorldApi)

        addClass {
            name = "${module.getName()}ViewModel"
            implements = "ContextModule"

            addMethod {
                name = "apply"
                overridesInterfaceMethod = true
                addArg {
                    name = "builder"
                    type = typeName("ContextBuilder")
                }

                setBody {
                    val builderOperations = expressionChainStatement {
                        instanceVariable("builder")
                    }

                    logic.allModuleElementTypes().forEach { type ->
                        builderOperations.then {
                            methodCall {
                                methodName = "setClass"
                                addGeneric(type.getName().value)
                                addArg {
                                    hardcodedExpression("InjectionMode.Prototype")
                                }
                            }
                        }
                    }
                    logic.windowsDef().forEach { window ->
                        builderOperations.then {
                            methodCall {
                                methodName = "addImpl"
                                addGeneric("Window")
                                addGeneric(window.getName())
                            }
                        }
                    }

                    add(builderOperations)
                }
            }
        }
    }

    override fun extraCSharpUsings(): List<String> = listOf(
        "B20.Architecture.Contexts.Api",
        "B20.ViewModel.Windows.Api",
    )

}