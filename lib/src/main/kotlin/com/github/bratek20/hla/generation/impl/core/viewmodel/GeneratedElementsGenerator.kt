package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class GeneratedElementsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getViewModelSubmodule()?.getElements()?.isNotEmpty() ?: false
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "OtherClassVm"
            partial = true
            extends {
                className = "UiElement"
                generic = typeName("OtherClass")
            }

            addField {
                type = typeName("Label")
                name = "id"
                getter = true
                setter = true
            }

            addField {
                type = typeName("Label")
                name = "amount"
                getter = true
                setter = true
            }

            addMethod {
                modifier = AccessModifier.PROTECTED
                override = true
                name = "onUpdate"

                setBody {
                    add(methodCallStatement {
                        target = getterField("id")
                        methodName = "update"
                        addArg {
                            getterFieldAccess {
                                objectRef = methodCall {
                                    target = getterField("model")
                                    methodName = "getId"
                                }
                                fieldName = "value"
                            }
                        }
                    })

                    add(methodCallStatement {
                        target = getterField("amount")
                        methodName = "update"
                        addArg {
                            methodCall {
                                target = getterField("model")
                                methodName = "getAmount"
                            }
                        }
                    })
                }
            }
        }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Traits",
            "B20.Frontend.UiElements"
        )
    }
}