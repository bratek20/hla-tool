package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.ViewModelElementDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.generation.impl.core.viewmodel.ViewModelElementLogic

class ElementViewLogic(
    private val elem: ViewModelElementLogic
) {
    fun getOps(mapper: ModelToViewModelTypeMapper): PerFileOperations {
        val def = elem.def
        val viewClassName = def.getModel().getName() + "View"
        return PerFileOperations(viewClassName) {
            addClass {
                name = viewClassName
                extends {
                    className = "ElementView"
                    addGeneric {
                        typeName(def.getName())
                    }
                }

                elem.getFields(mapper).forEach {
                    addField {
                        mutable = true
                        type = typeName(it.typeName + "View")
                        name = it.name

                        addAnnotation("SerializeField")
                    }
                }

                addMethod {
                    modifier = AccessModifier.PROTECTED
                    override = true
                    name = "onBind"

                    setBody {
                        add(methodCallStatement {
                            target = parent()
                            methodName = "onBind"
                        })

                        elem.getFields(mapper).forEach {
                            add(methodCallStatement {
                                target = variable(it.name)
                                methodName = "bind"
                                addArg {
                                    getterFieldAccess {
                                        objectRef = getterField("viewModel")
                                        fieldName = it.name
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }

}
class ElementsViewGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ElementsView
    }

    override fun shouldGenerate(): Boolean {
        return viewModelElementsDef().isNotEmpty()
    }

    override fun getOperationsPerFile(): List<PerFileOperations> {
        if(viewModelElementsDef()[0].getName() != "OtherClassVm") {
            return emptyList()
        }

        val elementsLogic = viewModelElementsLogic()
        val mapper = ModelToViewModelTypeMapper(elementsLogic)

        return elementsLogic.map { ElementViewLogic(it).getOps(mapper) }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}