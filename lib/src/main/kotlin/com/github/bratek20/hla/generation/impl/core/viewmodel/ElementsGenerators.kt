package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.generation.impl.core.view.WrappedElementViewLogic
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.mvvmtypesmappers.impl.ModelToViewModelTypeMapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.utils.camelToPascalCase
import kotlin.reflect.KClass
import kotlin.reflect.cast

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

    fun mapper(): ModelToViewModelTypeMapper {
        return ModelToViewModelTypeMapper(apiTypeFactory, typesWorldApi)
    }

    fun windowsLogic(): List<GeneratedWindowLogic> {
        return windowsDef().map { GeneratedWindowLogic(moduleDef.getName(), it, apiTypeFactory, typesWorldApi) }
    }

    fun allElementTypeNames(): List<String> {
        return elementsDef().map { it.getName() } +
            elementListTypesToGenerate().map { mapper().mapModelToViewModelTypeName(it.model) } +
            elementOptionalTypesToGenerate().map { mapper().mapModelToViewModelTypeName(it.model) } +
            elementEnumTypesToGenerate().map { mapper().mapModelToViewModelTypeName(it) }
    }

    private fun getAllModuleViewModelTypes(): List<WorldType> {
        val allTypes = typesWorldApi.getAllTypes()
        return allTypes.filter {
            it.getPath().asHla().getModuleName() == moduleDef.getName()
                    && it.getPath().asHla().getSubmoduleName() == SubmoduleName.ViewModel
                    && !it.getName().value.contains("<")
        }
    }
    
    private fun elementEnumTypesToGenerate(): List<EnumApiType> {
        val allEnumTypes = getAllModuleViewModelTypes()
            .filter {
                it.getName().value.endsWith("Switch")
            }
            .map {
                val modelType = getModelTypeForEnsuredUiElement(typesWorldApi, it.getName().value)
                val apiType = apiTypeFactory.create(createTypeDefinition(modelType.getName().value))
                apiType as EnumApiType
            }
        return allEnumTypes
    }

    fun elementOptionalTypesToGenerate(): List<OptionalElementViewModelLogic> {
        return getAllModuleViewModelTypes().filter {
            it.getName().value.startsWith("Optional")
        }.mapNotNull {
            val model = getModelTypeForEnsuredUiElement(typesWorldApi, it.getName().value)
            if (model.getPath().asHla().getModuleName() != moduleDef.getName()) {
                return@mapNotNull null
            }

            val typeDef = TypeDefinition.create(model.getName().value, listOf(
                TypeWrapper.OPTIONAL
            ))
            apiTypeFactory.create(typeDef) as OptionalApiType
        }.map {
            OptionalElementViewModelLogic(it, typesWorldApi)
        }
    }

    fun elementListTypesToGenerate(): List<ElementGroupViewModelLogic> {
        return getAllModuleViewModelTypes().filter {
            it.getName().value.endsWith("Group")
        }.map {
            val model = getModelTypeForEnsuredUiElement(typesWorldApi, it.getName().value)
            val typeDef = TypeDefinition.create(model.getName().value, listOf(
                TypeWrapper.LIST
            ))
            apiTypeFactory.create(typeDef) as ListApiType
        }.map {
            ElementGroupViewModelLogic(it, typesWorldApi)
        }
    }
}

class ViewModelField(
    val typeName: String,
    val name: String
) {
}

abstract class ViewModelLogic(
    val typesWorldApi: TypesWorldApi
) {

}

abstract class ViewModelWrapperLogic(
    typesWorldApi: TypesWorldApi
): ViewModelLogic(typesWorldApi) {

}

class ElementGroupViewModelLogic(
    val model: ListApiType,
    typesWorldApi: TypesWorldApi
): ViewModelWrapperLogic(typesWorldApi) {
}

class OptionalElementViewModelLogic(
    val model: OptionalApiType,
    typesWorldApi: TypesWorldApi
): ViewModelWrapperLogic(typesWorldApi) {
}

abstract class ViewModelElementLogic(
    val modelType: ApiTypeLogic,
    typesWorldApi: TypesWorldApi
): ViewModelLogic(typesWorldApi)  {
    abstract fun getTypeName(): String

    fun getType(): WorldType {
        return typesWorldApi.getTypeByName(WorldTypeName(getTypeName()))
    }

    abstract fun getClass(mapper: ModelToViewModelTypeMapper): ClassBuilderOps
}

class ViewModelEnumElementLogic(
    modelType: EnumApiType,
    typesWorldApi: TypesWorldApi
): ViewModelElementLogic(modelType, typesWorldApi) {
    override fun getTypeName(): String {
        return modelType.name() + "Switch"
    }

    override fun getClass(mapper: ModelToViewModelTypeMapper): ClassBuilderOps = {
        name = getTypeName()
        extends {
            className = "EnumSwitch"
            addGeneric {
                typeName(modelType.name())
            }
        }
    }
}

class ViewModelComplexElementLogic(
    val def: ViewModelElementDefinition,
    modelType: ComplexStructureApiType<*>,
    val apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi
): ViewModelElementLogic(modelType, typesWorldApi) {
    override fun getTypeName(): String = def.getName()

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

    override fun getClass(mapper: ModelToViewModelTypeMapper): ClassBuilderOps = {
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
    fun createComplexElementsLogic(defs: List<ViewModelElementDefinition>): List<ViewModelComplexElementLogic> {
        return defs.map { element ->
            val modelType = element.getModel()?.let { model ->
                apiTypeFactory.create(TypeDefinition(model.getName(), emptyList())) as ComplexStructureApiType<*>
            } ?: ComplexValueObjectApiType("EmptyModel", emptyList())

            modelType.init(apiTypeFactory.languageTypes, null)
            ViewModelComplexElementLogic(element, modelType, apiTypeFactory, typesWorldApi)
        }
    }

    fun createEnumElementsLogic(defs: List<EnumApiType>): List<ViewModelEnumElementLogic> {
        return defs.map { ViewModelEnumElementLogic(it, typesWorldApi) }
    }
}
class GeneratedElementsGenerator: BaseElementsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val elementsLogic = logic.elementsLogic()
        val mapper = logic.mapper()

        elementsLogic.forEach { element ->
            addClass(element.getClass(mapper))
        }

        logic.elementListTypesToGenerate().forEach {
            addClass(getClassForListType(mapper, it.model))
        }

        logic.elementOptionalTypesToGenerate().forEach {
            addClass(getClassForOptionalType(mapper, it.model))
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
        logic.elementsDef().forEach { element ->
            addClass {
                name = element.getName()
                partial = true
            }
        }
    }
}