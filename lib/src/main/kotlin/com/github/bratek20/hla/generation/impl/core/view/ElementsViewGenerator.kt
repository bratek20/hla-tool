package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.comment
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator

class ElementsViewGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ElementsView
    }

    override fun shouldGenerate(): Boolean {
        return viewModelElementsDef().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "OtherClassView"
            extends {
                className = "ElementView"
                addGeneric {
                    typeName("OtherClassVm")
                }
            }


            addField {
                type = typeName("LabelView")
                name = "id"

                addAnnotation("SerializeField")
            }

            addField {
                type = typeName("LabelView")
                name = "amount"

                addAnnotation("SerializeField")
            }
        }
        addExtraEmptyLines(7)
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}