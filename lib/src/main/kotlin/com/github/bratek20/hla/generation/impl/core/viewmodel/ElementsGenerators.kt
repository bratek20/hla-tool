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
            elementListTypesToGenerate().map { mapper().mapModelToViewModelTypeName(it) } +
            elementOptionalTypesToGenerate().map { mapper().mapModelToViewModelTypeName(it) } +
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

    fun elementOptionalTypesToGenerate(): List<OptionalApiType> {
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
        }
    }

    fun elementListTypesToGenerate(): List<ListApiType> {
        return getAllModuleViewModelTypes().filter {
            it.getName().value.endsWith("Group")
        }.map {
            val model = getModelTypeForEnsuredUiElement(typesWorldApi, it.getName().value)
            val typeDef = TypeDefinition.create(model.getName().value, listOf(
                TypeWrapper.LIST
            ))
            apiTypeFactory.create(typeDef) as ListApiType
        }
    }
}

class ViewModelField(
    val typeName: String,
    val name: String
) {
    companion object {
        fun fromDefs(moduleName: ModuleName, defs: List<FieldDefinition>, mapper: ModelToViewModelTypeMapper): List<ViewModelField> {
            return defs.map {
                val baseTypeName = it.getType().getName()
                val finalTypeName = if (it.getType().getWrappers().contains(TypeWrapper.LIST)) {
                    mapper.mapViewModelWrappedTypeToListType(baseTypeName)
                } else if (it.getType().getWrappers().contains(TypeWrapper.OPTIONAL)) {
                    mapper.mapViewModelWrappedTypeToOptionalType(baseTypeName)
                } else {
                    baseTypeName
                }
                ViewModelField(finalTypeName, it.getName())
            }
        }
    }
}

abstract class ViewModelElementLogic(
    val modelType: ApiTypeLogic
) {
    abstract fun getTypeName(): String

    abstract fun getClass(mapper: ModelToViewModelTypeMapper): ClassBuilderOps
}

class ViewModelEnumElementLogic(
    modelType: EnumApiType
): ViewModelElementLogic(modelType) {
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
    val typesWorldApi: TypesWorldApi
): ViewModelElementLogic(modelType) {
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
//    fun getFields(mapper: ModelToViewModelTypeMapper): List<ViewModelField> {
//        val result = mutableListOf<ViewModelField>()
//        getMappedFields().forEach { field ->
//            result.add(ViewModelField(
//                mapper.mapModelToViewModelTypeName(field.type),
//                field.name
//            ))
//        }
//
//        result.addAll(ViewModelField.fromDefs(ModuleName("TODO-FIX-ME_13131523"), def.getFields(), mapper))
//
//        return result
//    }

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

    fun <T : ApiTypeLogic> getMappedFieldsOfType(type: KClass<T>): List<T> {
        return getMappedFields().mapNotNull { field ->
            if (type.isInstance(field.type)) {
                type.cast(field.type)
            } else {
                null
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
        return defs.map { ViewModelEnumElementLogic(it) }
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
            addClass(getClassForListType(mapper, it))
        }

        logic.elementOptionalTypesToGenerate().forEach {
            addClass(getClassForOptionalType(mapper, it))
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