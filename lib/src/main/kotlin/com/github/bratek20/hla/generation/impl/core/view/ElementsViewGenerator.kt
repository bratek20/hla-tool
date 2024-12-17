package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PerFileOperations
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ViewModelLogic
import com.github.bratek20.hla.mvvmtypesmappers.impl.ViewModelToViewMapperLogic
import com.github.bratek20.hla.typesworld.api.*

open class ViewLogic(
    val viewModel: ViewModelLogic,
) {
    val typesWorldApi: TypesWorldApi = viewModel.typesWorldApi

    fun getViewClassName(): String {
        return getViewType().getName().value
    }

    fun getViewModelTypeName(): String {
        return viewModel.type.getName().value
    }

    fun getViewType(): WorldType {
        val viewTypeName = ViewModelToViewMapperLogic(typesWorldApi).map(viewModel.type).getName()
        return typesWorldApi.getTypeByName(viewTypeName)
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

class WrappedElementViewLogic(
    viewModel: ViewModelLogic
): ViewLogic(viewModel) {
    fun getElementViewType(): WorldType {
        return getExtendedParamType().getTypeArguments().first()
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
        return (logic.complexElementsLogic().map { ViewLogic(it) } +
                logic.elementListTypesToGenerate().map { WrappedElementViewLogic(it) } +
                logic.elementOptionalTypesToGenerate().map { WrappedElementViewLogic(it) } +
                logic.windowsLogic().map { ViewLogic(it) } +
                logic.enumElementsLogic().map { ViewLogic(it) })
            .map { it.getOps() }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Elements.View",
            "UnityEngine"
        )
    }
}