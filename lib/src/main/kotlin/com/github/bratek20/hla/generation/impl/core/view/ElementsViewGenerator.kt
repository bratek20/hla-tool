package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.apitypes.impl.ListApiType
import com.github.bratek20.hla.apitypes.impl.OptionalApiType
import com.github.bratek20.hla.apitypes.impl.WrappedApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.*
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelToViewMapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.ModelToViewModelTypeMapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.ViewModelToViewMapperLogic
import com.github.bratek20.hla.typesworld.api.*

abstract class ViewLogic(
    val viewModel: ViewModelLogic,
) {
    val typesWorldApi: TypesWorldApi = viewModel.typesWorldApi

    abstract fun getViewClassName(): String

    fun getViewType(): WorldType {
        return typesWorldApi.getTypeByName(WorldTypeName(getViewClassName()))
    }

    fun getExtendedParamType(): WorldConcreteParametrizedClass {
        val type = typesWorldApi.getTypeByName(WorldTypeName(getViewClassName()))
        val classType = typesWorldApi.getClassType(type)
        return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!)
    }

    fun getOps(): PerFileOperations {
        val type = typesWorldApi.getTypeByName(WorldTypeName(getViewClassName()))
        val extendedParamType = getExtendedParamType()

        return PerFileOperations(getViewClassName()) {
            addClass {
                name = type.getName().value
                extends {
                    className = extendedParamType.getType().getName().value.replaceAfter("<", "").dropLast(1)
                    extendedParamType.getTypeArguments().forEach {
                        addGeneric {
                            typeName(it.getName().value)
                        }
                    }
                }

                getBodyOps().invoke(this)
            }
        }
    }

    private fun getBodyOps(): ClassBuilderOps {
        val fields = getFields()
        if (fields.isEmpty()) {
            return {}
        }

        return {
            fields.forEach {
                addField {
                    mutable = true
                    type = typeName(it.getType().getName().value)
                    name = it.getName()

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

                    fields.forEach {
                        add(methodCallStatement {
                            target = variable(it.getName())
                            methodName = "bind"
                            addArg {
                                getterFieldAccess {
                                    objectRef = getterField("viewModel")
                                    fieldName = it.getName()
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    fun getFields(): List<WorldClassField> {
        val type = typesWorldApi.getTypeByName(WorldTypeName(getViewClassName()))
        val classType = typesWorldApi.getClassType(type)
        return classType.getFields()
    }
}

abstract class ContainerViewLogic(
    viewModel: ViewModelLogic
): ViewLogic(viewModel) {
    abstract fun getViewClassType(): WorldType
    abstract fun getViewModelTypeName(): String
}

class ComplexElementViewLogic(
    val elem: ViewModelComplexElementLogic,
    val mapper: ModelToViewModelTypeMapper
): ContainerViewLogic(elem) {
    public override fun getViewClassName(): String {
        return mapper.mapViewModelToViewTypeName(elem.getTypeName())
    }

    override fun getViewClassType(): WorldType {
        return mapper.mapViewModelNameToViewType(elem.getTypeName())
    }

    override fun getViewModelTypeName(): String {
        return elem.getTypeName()
    }
}

class WindowViewLogic(
    val window: GeneratedWindowLogic
): ContainerViewLogic(window) {
    public override fun getViewClassName(): String {
        return window.getClassName() + "View"
    }

    override fun getViewClassType(): WorldType {
        return WorldType.create(
            WorldTypeName(getViewClassName()),
            HlaTypePath.create(
                ModuleName(window.getModuleName()),
                SubmoduleName.View,
                PatternName.ElementsView
            ).asWorld()
        )
    }

    override fun getViewModelTypeName(): String {
        return window.getClassName()
    }
}

abstract class WrappedElementViewLogic(
    val modelType: WrappedApiType,
    val mapper: ModelToViewModelTypeMapper,
    viewModel: ViewModelLogic
): ViewLogic(viewModel) {
    override fun getViewClassName(): String {
        return mapper.mapModelToViewTypeName(modelType)
    }

    fun getElementViewType(): WorldType {
        return getExtendedParamType().getTypeArguments().first()
    }
}

class ElementGroupViewLogic(
    modelType: ListApiType,
    mapper: ModelToViewModelTypeMapper,
    viewModel: ViewModelLogic
): WrappedElementViewLogic(modelType, mapper, viewModel) {
}

class OptionalElementViewLogic(
    modelType: OptionalApiType,
    mapper: ModelToViewModelTypeMapper,
    viewModel: ViewModelLogic
): WrappedElementViewLogic(modelType, mapper, viewModel) {
}

class EnumElementViewLogic(
    val vmLogic: ViewModelEnumElementLogic,
    val mapper: ViewModelToViewMapper,
) : ViewLogic(vmLogic) {
    override fun getViewClassName(): String {
        return mapper.map(vmLogic.getType()).getName().value
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
        val typesWorldApi = mapper.typesWorldApi

        return (logic.complexElementsLogic().map { ComplexElementViewLogic(it, mapper) } +
                logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it.model, mapper, it) } +
                logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it.model, mapper, it) } +
                logic.windowsLogic().map { WindowViewLogic(it) } +
                logic.enumElementsLogic().map { EnumElementViewLogic(it, mapper.vmToViewMapper) })
            .map { it.getOps() }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}