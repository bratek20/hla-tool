package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.generation.impl.core.api.ListApiType
import com.github.bratek20.hla.generation.impl.core.api.OptionalApiType
import com.github.bratek20.hla.generation.impl.core.api.WrappedApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.generation.impl.core.viewmodel.ViewModelElementLogic

class ElementViewLogic(
    private val elem: ViewModelElementLogic,
    private val mapper: ModelToViewModelTypeMapper
) {
    fun getOps(): PerFileOperations {
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
                        type = typeName(mapper.mapViewModelToViewTypeName(it.typeName))
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

private abstract class WrappedElementViewLogic(
    private val modelType: WrappedApiType,
    private val mapper: ModelToViewModelTypeMapper
) {
    protected abstract fun extendedClassName(): String

    fun getOps(): PerFileOperations {
        val viewClassName = mapper.mapModelToViewTypeName(modelType)
        val elementViewTypeName = mapper.mapModelToViewTypeName(modelType.wrappedType)
        val elementViewModelTypeName = mapper.mapModelToViewModelTypeName(modelType.wrappedType)
        val elementModelTypeName = modelType.wrappedType.name()

        return PerFileOperations(viewClassName) {
            addClass {
                name = viewClassName
                extends {
                    className = extendedClassName()

                    addGeneric {
                        typeName(elementViewTypeName)
                    }
                    addGeneric {
                        typeName(elementViewModelTypeName)
                    }
                    addGeneric {
                        typeName(elementModelTypeName)
                    }
                }
            }
        }
    }
}

private class ElementGroupViewLogic(
    modelType: ListApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
    override fun extendedClassName(): String {
        return "UiElementGroupView"
    }
}

private class OptionalElementViewLogic(
    modelType: OptionalApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
    override fun extendedClassName(): String {
        return "OptionalUiElementView"
    }
}

class ElementsViewGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ElementsView
    }

    override fun shouldGenerate(): Boolean {
        return logic.elementsDef().isNotEmpty()
    }

    override fun getOperationsPerFile(): List<PerFileOperations> {
        val mapper = logic.mapper()
        return logic.elementsLogic().map { ElementViewLogic(it, mapper).getOps() } +
                logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it, mapper).getOps() } +
                logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper).getOps() }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}