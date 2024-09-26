package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.definitions.api.Attribute
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.ViewModelElementDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.*

class ViewModelElementLogic(
    private val def: ViewModelElementDefinition,
    val modelType: ComplexStructureApiType<*>
) {
    fun getType(): TypeBuilder = typeName(def.getName())

    private fun getMappedFields(): List<ComplexStructureField> {
        return def.getModel().getMappedFields().map { fieldName ->
            modelType.fields.first { it.name == fieldName }
        }
    }

    private fun getTraitTypesMethod(): MethodBuilderOps = {
        val traitTypes = def.getAttributes().map { mapAttributeToTraitType(it) }

        modifier = AccessModifier.PROTECTED
        override = true
        name = "getTraitTypes"
        returnType = listType(classType())

        setBody {
            add(returnStatement {
                newListOf(classType(), *traitTypes.toTypedArray())
            })
        }
    }

    private fun mapAttributeToTraitType(att: Attribute): ExpressionBuilder {
        return when (att.getName()) {
            "clickable" -> typeOf(typeName("Clickable"))
            "draggable" -> typeOf(typeName("Draggable"))
            else -> throw IllegalArgumentException("No trait mapping implemented: ${att.getName()}")
        }
    }

    private fun onUpdatedMethod(): MethodBuilderOps = {
        modifier = AccessModifier.PROTECTED
        override = true
        name = "onUpdate"

        setBody {
            getMappedFields().forEach { field ->
                add(methodCallStatement {
                    target = getterField(field.name)
                    methodName = "update"
                    addArg {
                        if (field.type is SimpleValueObjectApiType) {
                            getterFieldAccess {
                                objectRef = methodCall {
                                    target = getterField("model")
                                    methodName = field.getterName()
                                }
                                fieldName = "value"
                            }
                        }
                        else {
                            methodCall {
                                target = getterField("model")
                                methodName = field.getterName()
                            }
                        }
                    }
                })
            }
        }
    }

    fun getMappedFieldOfListType(): List<ListApiType> {
        return getMappedFields().mapNotNull { field ->
            if (field.type is ListApiType) {
                field.type as ListApiType
            } else {
                null
            }
        }
    }

    fun getClass(mapper: ModelToViewModelTypeMapper): ClassBuilderOps = {
        name = def.getName()
        partial = true
        extends {
            className = "UiElement"
            addGeneric {
                modelType.builder()
            }
        }

        getMappedFields().forEach { field ->
            addField {
                type = mapper.map(field.type)
                name = field.name
                getter = true
                setter = true
            }
        }

        def.getFields().forEach { field ->
            addField {
                type = typeName(field.getType().getName())
                name = field.getName()
                getter = true
                setter = true
            }
        }

        if (def.getAttributes().isNotEmpty()) {
            addMethod(getTraitTypesMethod())
        }
        addMethod(onUpdatedMethod())
    }
}

abstract class BaseElementsGenerator: PatternGenerator() {
    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    protected fun viewModelElements() = module.getViewModelSubmodule()?.getElements()

    override fun shouldGenerate(): Boolean {
        return viewModelElements()?.isNotEmpty() ?: false
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Traits",
            "B20.Frontend.UiElements"
        )
    }
}

class GeneratedElementsGenerator: BaseElementsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val listTypes: MutableList<ListApiType> = mutableListOf();

        val elementsLogic = viewModelElements()!!.map { element ->
            val modelTypeName = element.getModel().getName()
            val modelType = apiTypeFactory.create(TypeDefinition(modelTypeName, emptyList())) as ComplexStructureApiType<*>

            ViewModelElementLogic(element, modelType)
        }
        val mapper = ModelToViewModelTypeMapper(elementsLogic)

        elementsLogic.forEach { element ->
            addClass(element.getClass(mapper))

            listTypes.addAll(element.getMappedFieldOfListType())
        }

        listTypes.forEach {
            addClass(getClassForListType(it))
        }
    }

    //TODO-GENERALIZE
    private fun getClassForListType(listType: ListApiType): ClassBuilderOps = {
        name = "SomeClass2VmGroup"
        extends {
            className = "UiElementGroup"
            addGeneric {
                typeName("SomeClass2Vm")
            }
            addGeneric {
                typeName("SomeClass2")
            }
        }

        setConstructor {
            addArg {
                type = typeName("B20.Architecture.Contexts.Api.Context")
                name = "c"
            }
            addPassingArg {
                hardcodedExpression("() => c.Get<CreatedGameVm>()")
            }
        }
    }
}

class ElementsLogicGenerator: BaseElementsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ElementsLogic
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        viewModelElements()?.forEach { element ->
            addClass {
                name = element.getName()
                partial = true
            }
        }
    }
}