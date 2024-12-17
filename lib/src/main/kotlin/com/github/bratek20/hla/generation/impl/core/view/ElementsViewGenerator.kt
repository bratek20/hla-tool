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
import com.github.bratek20.hla.mvvmtypesmappers.impl.ModelToViewModelTypeMapper
import com.github.bratek20.hla.typesworld.api.*

abstract class ViewLogic(
    val typesWorldApi: TypesWorldApi
) {
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
    typesWorldApi: TypesWorldApi
): ViewLogic(typesWorldApi) {
    abstract fun getViewClassType(): WorldType
    abstract fun getViewModelTypeName(): String
}

class ComplexElementViewLogic(
    val elem: ViewModelComplexElementLogic,
    val mapper: ModelToViewModelTypeMapper
): ContainerViewLogic(mapper.typesWorldApi) {
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
    val window: GeneratedWindowLogic,
    typesWorldApi: TypesWorldApi
): ContainerViewLogic(typesWorldApi) {
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
    val mapper: ModelToViewModelTypeMapper
): ViewLogic(mapper.typesWorldApi) {
    override fun getViewClassName(): String {
        return mapper.mapModelToViewTypeName(modelType)
    }

    fun getElementViewType(): WorldType {
        return getExtendedParamType().getTypeArguments().first()
    }
}

class ElementGroupViewLogic(
    modelType: ListApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
}

class OptionalElementViewLogic(
    modelType: OptionalApiType,
    mapper: ModelToViewModelTypeMapper
): WrappedElementViewLogic(modelType, mapper) {
}

class EnumElementViewLogic(
    val vmLogic: ViewModelEnumElementLogic,
    val mapper: ModelToViewModelTypeMapper
) : ViewLogic(mapper.typesWorldApi) {
    override fun getViewClassName(): String {
        return mapper.mapModelToViewTypeName(vmLogic.modelType)
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
                logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it, mapper) } +
                logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper) } +
                logic.windowsLogic().map { WindowViewLogic(it, typesWorldApi) } +
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