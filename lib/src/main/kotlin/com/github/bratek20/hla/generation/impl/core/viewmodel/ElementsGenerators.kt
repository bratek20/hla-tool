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
import com.github.bratek20.utils.camelToPascalCase

class ViewModelElementLogic(
    private val def: ViewModelElementDefinition,
    val modelType: ComplexStructureApiType<*>
) {
    fun getTypeName(): String = def.getName()

    private fun getMappedFields(): List<ComplexStructureField> {
        return def.getModel().getMappedFields().map { fieldName ->
            modelType.fields.firstOrNull { it.name == fieldName }
                ?: throw IllegalArgumentException("Field not found: $fieldName in ${modelType.name} for ${def.getName()}")
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
        return typeOf(typeName(camelToPascalCase(att.getName())))
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
                        //TODO-REF
                        if (field.type is SimpleValueObjectApiType) {
                            getterFieldAccess {
                                objectRef = methodCall {
                                    target = getterField("model")
                                    methodName = field.getterName()
                                }
                                fieldName = "value"
                            }
                        }
                        else if (field.type is OptionalApiType && (field.type as OptionalApiType).wrappedType is SimpleValueObjectApiType) {
                            optionalOp {
                                methodCall {
                                    target = getterField("model")
                                    methodName = field.getterName()
                                }
                            }.map {
                                getterFieldAccess {
                                    objectRef = variable("it")
                                    fieldName = "value"
                                }
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
        return getMappedFieldOfType<ListApiType>()
    }

    fun getMappedFieldOfOptionalType(): List<OptionalApiType> {
        return getMappedFieldOfType<OptionalApiType>()
    }

    private inline fun <reified T: ApiType> getMappedFieldOfType(): List<T> {
        return getMappedFields().mapNotNull { field ->
            if (field.type is T) {
                field.type as T
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
                type = typeName(mapper.mapModelToViewModelTypeName(field.type))
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

abstract class BaseElementsGenerator: BaseViewModelPatternGenerator() {
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
        val optionalTypes: MutableList<OptionalApiType> = mutableListOf();

        val elementsLogic = viewModelElements()!!.map { element ->
            val modelTypeName = element.getModel().getName()
            val modelType = apiTypeFactory.create(TypeDefinition(modelTypeName, emptyList())) as ComplexStructureApiType<*>

            ViewModelElementLogic(element, modelType)
        }
        val mapper = ModelToViewModelTypeMapper(elementsLogic)

        elementsLogic.forEach { element ->
            addClass(element.getClass(mapper))

            listTypes.addAll(element.getMappedFieldOfListType())
            optionalTypes.addAll(element.getMappedFieldOfOptionalType())
        }

        viewModelWindowsLogic().forEach { window ->
            window.getElementTypesWrappedInList().forEach {
                listTypes.add(ListApiType(mapper.getModelForViewModelType(it)))
            }
        }

        listTypes.forEach {
            addClass(getClassForListType(mapper, it))
        }

        optionalTypes.forEach {
            if (it.wrappedType is ComplexStructureApiType<*>) {
                addClass(getClassForOptionalType(mapper, it))
            }
        }

    }

    private fun getClassForListType(mapper: ModelToViewModelTypeMapper, listType: ListApiType): ClassBuilderOps = {
        val listTypeName = mapper.mapModelToViewModelTypeName(listType)
        val elementTypeName = mapper.mapModelToViewModelTypeName(listType.wrappedType)
        val elementModelTypeName = listType.wrappedType.name()

        name = listTypeName
        extends {
            className = "UiElementGroup"
            addGeneric {
                typeName(elementTypeName)
            }
            addGeneric {
                typeName(elementModelTypeName)
            }
        }

        setConstructor {
            addArg {
                type = typeName("B20.Architecture.Contexts.Api.Context")
                name = "c"
            }
            addPassingArg {
                //TODO-REF
                hardcodedExpression("() => c.Get<$elementTypeName>()")
            }
        }
    }

    private fun getClassForOptionalType(mapper: ModelToViewModelTypeMapper, optionalType: OptionalApiType): ClassBuilderOps = {
        val optionalTypeName = mapper.mapModelToViewModelTypeName(optionalType)
        val elementTypeName = mapper.mapModelToViewModelTypeName(optionalType.wrappedType)
        val elementModelTypeName = optionalType.wrappedType.name()

        name = optionalTypeName
        extends {
            className = "OptionalUiElement"
            addGeneric {
                typeName(elementTypeName)
            }
            addGeneric {
                typeName(elementModelTypeName)
            }
        }

        setConstructor {
            addArg {
                type = typeName(elementTypeName)
                name = "element"
            }
            addPassingArg {
                variable("element")
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