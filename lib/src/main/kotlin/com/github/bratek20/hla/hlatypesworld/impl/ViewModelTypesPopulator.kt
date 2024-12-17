package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.apitypes.impl.ComplexValueObjectApiType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.*
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelTypesCalculator
import com.github.bratek20.hla.mvvmtypesmappers.impl.BaseViewModelTypesMapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.*

class ViewModelTypesPopulator(
    private val world: TypesWorldApi,
    private val worldQueries: HlaTypesWorldQueries,
    private val vmTypesCalculator: ViewModelTypesCalculator
): HlaTypesWorldPopulator {
    companion object {
        const val ORDER = ApiTypesPopulator.ORDER + 1
    }
    override fun getOrder(): Int {
        return ORDER
    }

    lateinit var apiTypeFactory: ApiTypeFactoryLogic
    private val mapper = BaseViewModelTypesMapper()

    override fun populate(modules: List<ModuleDefinition>) {
        //TODO-REF
        if (!::apiTypeFactory.isInitialized) return

        modules.forEach { populateElements(it) }
        modules.forEach { populateWindows(it) }
        modules.forEach { populateEnumSwitches(it) }
        modules.forEach { populateElementGroups(it) }
        modules.forEach { populateOptionalElements(it) }
    }

    private fun populateWindows(module: ModuleDefinition) {
        module.getViewModelSubmodule()?.let {
            it.getWindows().forEach { window ->
                val path = HlaTypePath.create(
                    module.getName(),
                    SubmoduleName.ViewModel,
                    PatternName.GeneratedWindows
                ).asWorld()

                val modelName = window.getName() + "State"
                val paramType = WorldType.create(
                    WorldTypeName("Window<${modelName}>"),
                    path
                )

                world.addConcreteParametrizedClass(
                    WorldConcreteParametrizedClass.create(
                        type = paramType,
                        typeArguments = listOf(
                            WorldType.create(WorldTypeName(modelName), path)
                        )
                    )
                )

                world.addClassType(
                    WorldClassType.create(
                        type = WorldType.create(
                            name = WorldTypeName(window.getName()),
                            path = path
                        ),
                        extends = paramType,
                        fields = getFieldsForWindow(window),
                    )
                )
            }
        }
    }

    private fun populateElements(module: ModuleDefinition) {
        module.getViewModelSubmodule()?.let {
            it.getElements().forEach { element ->
                val path = HlaTypePath.create(
                    module.getName(),
                    SubmoduleName.ViewModel,
                    PatternName.GeneratedElements
                ).asWorld()

                val modelName = element.getModel()?.getName() ?: "EmptyModel"
                val paramTypeName = WorldTypeName("UiElement<${modelName}>")

                val paramType = if (!world.hasTypeByName(paramTypeName)) {
                    val paramType = WorldType.create(
                        paramTypeName,
                        path
                    )

                    world.addConcreteParametrizedClass(
                        WorldConcreteParametrizedClass.create(
                            type = paramType,
                            typeArguments = listOf(
                                world.getTypeByName(WorldTypeName(modelName))
                            )
                        )
                    )

                    paramType
                } else {
                    world.getTypeByName(paramTypeName)
                }

                world.addClassType(
                    WorldClassType.create(
                        type = WorldType.create(
                            name = WorldTypeName(element.getName()),
                            path = path
                        ),
                        fields = getFieldsForElement(element),
                        extends = paramType
                    )
                )
            }
        }
    }

    private fun populateEnumSwitches(module: ModuleDefinition) {
        val ensuredEnumSwitches = world.getAllTypes().filter {
            it.getName().value.endsWith("Switch") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        val ensuredEnumSwitchGroups = world.getAllTypes().filter {
            it.getName().value.endsWith("SwitchGroup") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        val extractedEnumSwitchesFromGroups = ensuredEnumSwitchGroups.map { group ->
            val enumSwitch = group.getName().value.removeSuffix("Group")
            WorldType.create(
                name = WorldTypeName(enumSwitch),
                path = group.getPath()
            )
        }

        val enumSwitches = ensuredEnumSwitches + extractedEnumSwitchesFromGroups

        enumSwitches.forEach { enumSwitch ->
            val enumType = world.getTypeByName(WorldTypeName(enumSwitch.getName().value.replace("Switch", "")))

            val paramType = WorldType.create(
                WorldTypeName("EnumSwitch<${enumType.getName()}>"),
                enumSwitch.getPath()
            )
            world.addConcreteParametrizedClass(
                WorldConcreteParametrizedClass.create(
                    type = paramType,
                    typeArguments = listOf(
                        enumType
                    )
                )
            )

            world.addClassType(
                WorldClassType.create(
                    type = enumSwitch,
                    fields = emptyList(),
                    extends = paramType
                )
            )
        }
    }

    private fun populateElementWrappers(
        module: ModuleDefinition,
        nameChecker: (String) -> Boolean,
        wrappedTypeNameExtractor: (String) -> String,
        wrappingClassName: String
    ) {
        val ensuredGroups = worldQueries.getAll(module.getName(), SubmoduleName.ViewModel).filter {
            world.getTypeInfo(it).getKind() != WorldTypeKind.ConcreteParametrizedClass && nameChecker(it.getName().value)
        }

        ensuredGroups.forEach {
            val wrappedTypeName = wrappedTypeNameExtractor(it.getName().value)
            val modelType = getModelTypeForEnsuredUiElement(world, wrappedTypeName)
            val viewModelType = world.getTypeByName(WorldTypeName(wrappedTypeName))
            val paramType = WorldType.create(
                WorldTypeName("${wrappingClassName}<${viewModelType.getName()},${modelType.getName()}>"),
                it.getPath()
            )
            world.addConcreteParametrizedClass(
                WorldConcreteParametrizedClass.create(
                    type = paramType,
                    typeArguments = listOf(
                        viewModelType,
                        modelType
                    )
                )
            )
            world.addClassType(
                WorldClassType.create(
                    type = it,
                    fields = emptyList(),
                    extends = paramType
                )
            )
        }
    }

    private fun populateElementGroups(module: ModuleDefinition) {
        populateElementWrappers(
            module,
            { it.endsWith("Group") },
            { it.removeSuffix("Group") },
            "UiElementGroup"
        )
    }

    private fun populateOptionalElements(module: ModuleDefinition) {
        populateElementWrappers(
            module,
            { it.startsWith("Optional") },
            { it.removePrefix("Optional") },
            "OptionalUiElement"
        )
    }

    private fun getFieldsForElement(def: ViewModelElementDefinition): List<WorldClassField> {
        val modelFields = def.getModel()?.let { model ->
            model.getMappedFields().map {
                val type = mapModelField(model.getName(), it)
                WorldClassField.create(it, type)
            }
        } ?: emptyList()

        return modelFields + mapViewModelFields(def.getFields())
    }

    private fun viewModelTypeDefToWorldType(def: TypeDefinition): WorldTypeName {
        if (def.getWrappers().contains(TypeWrapper.LIST)) {
            return viewModelTypeDefToWorldTypeForWrapper(def, vmTypesCalculator::wrapWithGroup)
        }
        if (def.getWrappers().contains(TypeWrapper.OPTIONAL)) {
            return viewModelTypeDefToWorldTypeForWrapper(def, vmTypesCalculator::wrapWithOptional)
        }
        return WorldTypeName(def.getName())
    }

    private fun viewModelTypeDefToWorldTypeForWrapper(
        def: TypeDefinition,
        wrappingFun: (WorldType) -> WorldType,
    ): WorldTypeName {
        val wrappedType = world.getTypeByName(WorldTypeName(def.getName()))
        val wrapperType = wrappingFun(wrappedType)
        world.ensureType(wrapperType)
        return wrapperType.getName()
    }

    private fun getFieldsForWindow(def: ViewModelWindowDefinition): List<WorldClassField> {
        return mapViewModelFields(def.getFields())
    }

    private fun mapModelField(modelTypeName: String, fieldName: String): WorldType {
        val modelType = apiTypeFactory.create(createTypeDefinition(modelTypeName)) as ComplexValueObjectApiType
        val field = modelType.fields.find { it.name == fieldName }
            ?: throw IllegalStateException("Field $fieldName not found in model $modelTypeName")
        return mapper.mapModelToViewModelType(field.type)
    }

    private fun mapViewModelFields(defs: List<FieldDefinition>): List<WorldClassField> {
        return defs.map {
            world.getTypeByName(viewModelTypeDefToWorldType(it.getType())).let { type ->
                WorldClassField.create(it.getName(), type)
            }
        }
    }
}