package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.generation.impl.core.api.ListApiType
import com.github.bratek20.hla.generation.impl.core.api.OptionalApiType
import com.github.bratek20.hla.generation.impl.core.api.WrappedApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.*
import com.github.bratek20.hla.type.api.HlaType
import com.github.bratek20.hla.type.api.HlaTypePath
import com.github.bratek20.hla.type.api.emptyHlaTypePath

abstract class ViewLogic(
    val mapper: ModelToViewModelTypeMapper
) {
    abstract fun getOps(): PerFileOperations
}

abstract class ContainerViewLogic(
    mapper: ModelToViewModelTypeMapper
): ViewLogic(mapper) {
    protected abstract fun getViewClassName(): String
    abstract fun getViewClassType(): HlaType
    abstract fun getViewModelTypeName(): String
    abstract fun getFields(): List<ViewModelField>
    protected abstract fun getExtendedClassName(): String

    override fun getOps(): PerFileOperations {
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
                    overridesClassMethod = true
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

class ComplexElementViewLogic(
    val elem: ViewModelComplexElementLogic,
    mapper: ModelToViewModelTypeMapper
): ContainerViewLogic(mapper) {
    public override fun getViewClassName(): String {
        return mapper.mapViewModelToViewTypeName(elem.getTypeName())
    }

    override fun getViewClassType(): HlaType {
        return mapper.mapModelToViewType(elem.modelType)
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
): ContainerViewLogic(mapper) {
    public override fun getViewClassName(): String {
        return window.getClassName() + "View"
    }

    override fun getViewClassType(): HlaType {
        return HlaType.create(
            getViewClassName(),
            //TODO-GENERALIZE
            HlaTypePath.create(ModuleName("SomeModule"), SubmoduleName.View)
        )
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

abstract class WrappedElementViewLogic(
    val modelType: WrappedApiType,
    mapper: ModelToViewModelTypeMapper
): ViewLogic(mapper) {
    protected abstract fun extendedClassName(): String

    fun getViewClassName(): String {
        return mapper.mapModelToViewTypeName(modelType)
    }

    fun getViewClassType(): HlaType {
        return mapper.mapModelToViewType(modelType)
    }

    fun getElementViewModelTypeName(): String {
        return mapper.mapModelToViewModelTypeName(modelType.wrappedType)
    }

    override fun getOps(): PerFileOperations {
        val viewClassName = getViewClassName()
        val elementViewTypeName = mapper.mapModelToViewTypeName(modelType.wrappedType)
        val elementViewModelTypeName = getElementViewModelTypeName()
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

class ElementGroupViewLogic(
    modelType: ListApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
    override fun extendedClassName(): String {
        return "UiElementGroupView"
    }
}

class OptionalElementViewLogic(
    modelType: OptionalApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
    override fun extendedClassName(): String {
        return "OptionalUiElementView"
    }
}

class EnumElementViewLogic(
    val vmLogic: ViewModelEnumElementLogic,
    mapper: ModelToViewModelTypeMapper
) : ViewLogic(mapper) {
    fun getViewClassName(): String {
        return mapper.mapModelToViewTypeName(vmLogic.modelType)
    }

    fun getViewClassType(): HlaType {
        return mapper.mapModelToViewType(vmLogic.modelType)
    }

    override fun getOps(): PerFileOperations {
        return PerFileOperations(getViewClassName()) {
            addClass {
                name = getViewClassName()
                extends {
                    className = "EnumSwitchView"
                    addGeneric {
                        typeName(vmLogic.modelType.name())
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
        return logic.elementsDef().isNotEmpty()
    }

    override fun getOperationsPerFile(): List<PerFileOperations> {
        val mapper = logic.mapper()
        return (logic.complexElementsLogic().map { ComplexElementViewLogic(it, mapper) } +
                logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it, mapper) } +
                logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper) } +
                logic.windowsLogic().map { WindowViewLogic(it, mapper) } +
                logic.enumElementsLogic().map { EnumElementViewLogic(it, mapper) })
            .map { it.getOps() }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}