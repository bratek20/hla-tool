package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.generation.impl.core.api.ListApiType
import com.github.bratek20.hla.generation.impl.core.api.OptionalApiType
import com.github.bratek20.hla.generation.impl.core.api.WrappedApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.*

abstract class BaseViewLogic(
    protected val mapper: ModelToViewModelTypeMapper
) {
    protected abstract fun getViewClassName(): String
    protected abstract fun getViewModelTypeName(): String
    protected abstract fun getFields(): List<ViewModelField>
    protected abstract fun getExtendedClassName(): String

    fun getOps(): PerFileOperations {
        val viewClassName = getViewClassName()
        return PerFileOperations(viewClassName) {
            addClass {
                name = viewClassName
                extends {
                    className = getExtendedClassName()
                    addGeneric {
                        typeName(getViewModelTypeName())
                    }
                }

                getFields().forEach {
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

                        getFields().forEach {
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

class ElementViewLogic(
    val elem: ViewModelElementLogic,
    mapper: ModelToViewModelTypeMapper
): BaseViewLogic(mapper) {
    override fun getViewClassName(): String {
        return mapper.mapViewModelToViewTypeName(elem.getTypeName())
    }

    override fun getViewModelTypeName(): String {
        return elem.getTypeName()
    }

    override fun getFields(): List<ViewModelField> {
        return elem.getFields(mapper)
    }

    override fun getExtendedClassName(): String {
        return "ElementView"
    }

}

class WindowViewLogic(
    val window: GeneratedWindowLogic,
    mapper: ModelToViewModelTypeMapper
): BaseViewLogic(mapper) {
    override fun getViewClassName(): String {
        return window.getClassName() + "View"
    }

    override fun getViewModelTypeName(): String {
        return window.getClassName()
    }

    override fun getFields(): List<ViewModelField> {
        return window.getFields(mapper)
    }

    override fun getExtendedClassName(): String {
        return "WindowView"
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
                logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper).getOps() } +
                logic.windowsLogic().map { WindowViewLogic(it, mapper).getOps() }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}