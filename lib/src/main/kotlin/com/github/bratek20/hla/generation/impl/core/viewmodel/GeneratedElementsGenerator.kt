package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.ViewModelElementDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.*

class ViewModelElementLogic(
    private val def: ViewModelElementDefinition,
    private val modelType: ComplexStructureApiType<*>
) {

    private fun getMappedFields(): List<ComplexStructureField> {
        return def.getModel().getMappedFields().map { fieldName ->
            modelType.fields.first { it.name == fieldName }
        }
    }

    private fun mapModelTypeToViewModelType(modelType: ApiType): TypeBuilder {
        if (modelType is BaseApiType) {
            return typeName("Label")
        }
        if (modelType is SimpleStructureApiType) {
            return typeName("Label")
        }
        throw IllegalArgumentException("No view model mapping implemented: $modelType")
    }

    fun getClass(): ClassBuilderOps = {
        name = def.getName()
        partial = true
        extends {
            className = "UiElement"
            generic = modelType.builder()
        }

        getMappedFields().forEach { field ->
            addField {
                type = mapModelTypeToViewModelType(field.type)
                name = field.name
                getter = true
                setter = true
            }
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

class GeneratedElementsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    private fun viewModelElements() = module.getViewModelSubmodule()?.getElements()

    override fun shouldGenerate(): Boolean {
        return viewModelElements()?.isNotEmpty() ?: false
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        viewModelElements()?.forEach { element ->
            val modelTypeName = element.getModel().getName()
            val modelType = apiTypeFactory.create(TypeDefinition(modelTypeName, emptyList())) as ComplexStructureApiType<*>

            val logic = ViewModelElementLogic(element, modelType)
            addClass(logic.getClass())
        }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Traits",
            "B20.Frontend.UiElements"
        )
    }
}