package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.ViewModelElementDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.api.ComplexValueObjectApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelTypesMapper
import com.github.bratek20.hla.generation.impl.core.viewmodel.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.*

class ViewModelTypesPopulator(
    private val world: TypesWorldApi,
    private val apiTypeFactory: ApiTypeFactory
): HlaTypesWorldPopulator {

    override fun populate(modules: List<ModuleDefinition>) {
        modules.forEach { populateElements(it) }
        modules.forEach { populateEnumSwitches(it) }
        modules.forEach { populateElementGroups(it) }
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
                val paramType = WorldType.create(
                    WorldTypeName("UiElement<${modelName}>"),
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

    private fun populateElementGroups(module: ModuleDefinition) {
        val ensuredGroups = world.getAllTypes().filter {
            it.getName().value.endsWith("Group") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        ensuredGroups.forEach {
            val wrappedTypeName = it.getName().value.removeSuffix("Group")
            val modelType = getModelTypeForEnsuredUiElement(world, wrappedTypeName)
            val viewModelType = world.getTypeByName(WorldTypeName(wrappedTypeName))
            val paramType = WorldType.create(
                WorldTypeName("UiElementGroup<${viewModelType.getName()},${modelType.getName()}>"),
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

    private fun getFieldsForElement(def: ViewModelElementDefinition): List<WorldClassField> {
        return def.getModel()?.let { model ->
            model.getMappedFields().map {
                val type = mapModelField(model.getName(), it)
                WorldClassField.create(it, type)
            }
        } ?: emptyList()
    }

    private fun mapModelField(modelTypeName: String, fieldName: String): WorldType {
        val modelType = apiTypeFactory.create(createTypeDefinition(modelTypeName)) as ComplexValueObjectApiType
        val field = modelType.fields.find { it.name == fieldName }
            ?: throw IllegalStateException("Field $fieldName not found in model $modelTypeName")
        return mapper.mapModelToViewModelType(field.type)
    }

    companion object {
        val mapper = BaseViewModelTypesMapper()
    }

    override fun getOrder(): Int {
        return 2
    }
}