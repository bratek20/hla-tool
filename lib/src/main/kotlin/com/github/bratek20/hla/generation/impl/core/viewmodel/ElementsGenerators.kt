package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.ComplexStructureField
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElementWrapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.getViewModelTypeForEnsuredElementWrapper
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeKind
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.utils.camelToPascalCase

class ViewModelSharedLogic(
    private val moduleDef: ModuleDefinition,
    private val apiTypeFactory: ApiTypeFactoryLogic,
    private val typesWorldApi: TypesWorldApi
) {
    fun windowsDef(): List<ViewModelWindowDefinition> =
        moduleDef.getViewModelSubmodule()?.getWindows() ?: emptyList()

    fun elementsDef(): List<ViewModelElementDefinition> =
        moduleDef.getViewModelSubmodule()?.getElements() ?: emptyList()

    fun elementsLogic(): List<ViewModelElementLogic> {
        return complexElementsLogic() + enumElementsLogic()
    }

    fun complexElementsLogic(): List<ViewModelComplexElementLogic> {
        return ViewModelLogicFactory(apiTypeFactory, typesWorldApi).createComplexElementsLogic(elementsDef())
    }

    fun enumElementsLogic(): List<ViewModelEnumElementLogic> {
        return ViewModelLogicFactory(apiTypeFactory, typesWorldApi).createEnumElementsLogic(elementEnumTypesToGenerate())
    }

    fun windowsLogic(): List<GeneratedWindowLogic> {
        return windowsDef().map { GeneratedWindowLogic(moduleDef.getName(), it, apiTypeFactory, typesWorldApi, typesWorldApi.getTypeByName(WorldTypeName(it.getName()))) }
    }

    fun allModuleElementTypes(): List<WorldType> {
        val allTypes = typesWorldApi.getAllTypes()
        return allTypes.filter {
            it.getPath().asHla().getModuleName() == moduleDef.getName()
                    && it.getPath().asHla().getSubmoduleName() == SubmoduleName.ViewModel
                    && it.getPath().asHla().getPatternName() == PatternName.GeneratedElements
                    && typesWorldApi.getTypeInfo(it).getKind() == WorldTypeKind.ClassType
        }
    }
    
    private fun elementEnumTypesToGenerate(): List<WorldType> {
        return allModuleElementTypes()
            .filter {
                it.getName().value.endsWith("Switch")
            }
    }

    fun elementOptionalTypesToGenerate(): List<OptionalElementViewModelLogic> {
        return allModuleElementTypes().filter {
            it.getName().value.startsWith("Optional")
        }.map {
            OptionalElementViewModelLogic(typesWorldApi, it)
        }
    }

    fun elementListTypesToGenerate(): List<ElementGroupViewModelLogic> {
        return allModuleElementTypes().filter {
            it.getName().value.endsWith("Group")
        }.map {
            ElementGroupViewModelLogic(typesWorldApi, it)
        }
    }
}

class ViewModelField(
    val typeName: String,
    val name: String
) {
}

abstract class ViewModelLogic(
    val typesWorldApi: TypesWorldApi,
    val type: WorldType
) {
    fun getTypeName(): String {
        return type.getName().value
    }
}

abstract class ViewModelWrapperLogic(
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelLogic(typesWorldApi, type) {

}

class ElementGroupViewModelLogic(
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelWrapperLogic(typesWorldApi, type) {
    fun getClass(): ClassBuilderOps = {
        val listTypeName = type.getName().value
        val elementTypeName = getViewModelTypeForEnsuredElementWrapper(typesWorldApi, listTypeName).getName().value
        val elementModelTypeName = getModelTypeForEnsuredUiElementWrapper(typesWorldApi, listTypeName).getName().value

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
}

class OptionalElementViewModelLogic(
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelWrapperLogic(typesWorldApi, type) {
    fun getClass(): ClassBuilderOps = {
        val optionalTypeName = type.getName().value
        val elementTypeName = getViewModelTypeForEnsuredElementWrapper(typesWorldApi, optionalTypeName).getName().value
        val elementModelTypeName = getModelTypeForEnsuredUiElementWrapper(typesWorldApi, optionalTypeName).getName().value

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

abstract class ViewModelElementLogic(
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelLogic(typesWorldApi, type)  {
    abstract fun getClass(): ClassBuilderOps
}

class ViewModelEnumElementLogic(
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelElementLogic(typesWorldApi, type) {
    override fun getClass(): ClassBuilderOps = {
        name = getTypeName()
        extends {
            className = "EnumSwitch"
            addGeneric {
                typeName(getModelTypeForEnsuredUiElement(typesWorldApi, getTypeName()).getName().value)
            }
        }
    }
}

class ViewModelComplexElementLogic(
    val def: ViewModelElementDefinition,
    val modelType: ComplexStructureApiType<*>,
    val apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelElementLogic(typesWorldApi, type) {
    fun getFields(): List<ViewModelField> {
        val type = typesWorldApi.getTypeByName(WorldTypeName(def.getName()))
        val classType = typesWorldApi.getClassType(type)
        return classType.getFields().map { field ->
            ViewModelField(
                typeName = field.getType().getName().value,
                name = field.getName(),
            )
        }
    }

    private fun getMappedFields(): List<ComplexStructureField> {
        return def.getModel()?.getMappedFields()?.map { fieldName ->
            (modelType as ComplexStructureApiType<*>).fields.firstOrNull { it.name == fieldName }
                ?: throw IllegalArgumentException("Field not found: $fieldName in ${modelType.name} for ${def.getName()}")
        } ?: emptyList()
    }

    private fun getTraitTypesMethod(): MethodBuilderOps = {
        val traitTypes = def.getAttributes().map { mapAttributeToTraitType(it) }

        modifier = AccessModifier.PROTECTED
        overridesClassMethod = true
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
        overridesClassMethod = true
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

    override fun getClass(): ClassBuilderOps = {
        name = def.getName()
        partial = true
        extends {
            className = "UiElement"
            addGeneric {
                modelType.builder()
            }
        }

        getFields().forEach { field ->
            addField {
                type = typeName(field.typeName)
                name = field.name
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
    override fun shouldGenerate(): Boolean {
        return logic.elementsDef().isNotEmpty()
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Traits",
            "B20.Frontend.UiElements"
        )
    }
}

class ViewModelLogicFactory(
    private val apiTypeFactory: ApiTypeFactoryLogic,
    private val typesWorldApi: TypesWorldApi
) {
    //TODO
    fun createComplexElementsLogic(defs: List<ViewModelElementDefinition>): List<ViewModelComplexElementLogic> {
        return defs.map { element ->
            val modelType = element.getModel()?.let { model ->
                apiTypeFactory.create(TypeDefinition(model.getName(), emptyList())) as ComplexStructureApiType<*>
            } ?: ComplexValueObjectApiType("EmptyModel", emptyList())

            modelType.init(apiTypeFactory.languageTypes, null)
            ViewModelComplexElementLogic(element, modelType, apiTypeFactory, typesWorldApi, typesWorldApi.getTypeByName(WorldTypeName(element.getName())))
        }
    }

    fun createEnumElementsLogic(defs: List<WorldType>): List<ViewModelEnumElementLogic> {
        return defs.map { ViewModelEnumElementLogic(typesWorldApi, it) }
    }
}
class GeneratedElementsGenerator: BaseElementsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val elementsLogic = logic.elementsLogic()

        elementsLogic.forEach { element ->
            addClass(element.getClass())
        }

        logic.elementListTypesToGenerate().forEach {
            addClass(it.getClass())
        }

        logic.elementOptionalTypesToGenerate().forEach {
            addClass(it.getClass())
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
        logic.elementsDef().forEach { element ->
            addClass {
                name = element.getName()
                partial = true
            }
        }
    }
}