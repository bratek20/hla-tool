package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.mvvmtypesmappers.impl.ModelToViewModelTypeMapper
import com.github.bratek20.hla.mvvmtypesmappers.impl.getViewModelTypeForEnsuredElementWrapper
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.typesworld.api.*

class ViewTypesPopulator(
    private val world: TypesWorldApi
): HlaTypesWorldPopulator {
    companion object {
        const val ORDER = ViewModelTypesPopulator.ORDER + 1
    }
    override fun getOrder(): Int {
        return ORDER
    }

    lateinit var mapper: ModelToViewModelTypeMapper

    override fun populate(modules: List<ModuleDefinition>) {
        modules.forEach { populate(it) }
    }

    private fun populate(module: ModuleDefinition) {
        val viewModelTypes = world.getAllTypes().filter {
            it.getPath().asHla().getSubmoduleName() == SubmoduleName.ViewModel &&
                it.getPath().asHla().getModuleName() == module.getName() &&
                    !it.getName().value.contains("<")
        }

        viewModelTypes.forEach { type ->
            val classType: WorldClassType
            //TODO-REF can be removed when types world population fixed
            try {
                classType = world.getClassType(type)
            }
            catch (e: WorldTypeNotFoundException) {
                return@forEach
            }

            val viewClassType = mapper.mapViewModelToViewType(classType.getType())
            if (world.hasTypeByName(viewClassType.getName())) {
                return@forEach
            }

            val viewFields = classType.getFields().mapNotNull { field ->
                try {
                    WorldClassField.create(
                        field.getName(),
                        mapper.mapViewModelToViewType(field.getType())
                    )
                } catch (e: WorldTypeNotFoundException) {
                    null
                }
            }

            world.addClassType(
                WorldClassType.create(
                    type = viewClassType,
                    extends = populateExtendTypeIfPresent(classType, viewClassType),
                    fields = viewFields
                )
            )
        }
    }

    private fun populateExtendTypeIfPresent(viewModel: WorldClassType, view: WorldType): WorldType? {
        return populateExtendWrapperTypeIfPresent(
            viewModel, view,
            { it.startsWith("UiElementGroup") },
            "UiElementGroupView"
        ) ?: populateExtendWrapperTypeIfPresent(
            viewModel, view,
            { it.startsWith("OptionalUiElement") },
            "OptionalUiElementView"
        )
    }

    private fun populateExtendWrapperTypeIfPresent(
        viewModel: WorldClassType,
        view: WorldType,
        nameChecker: (String) -> Boolean,
        wrapperName: String
    ): WorldType? {
        if (viewModel.getExtends() == null) {
            return null
        }

        var viewExtends: WorldType? = null
        if (nameChecker(viewModel.getExtends()!!.getName().value)) {
            val wrappedViewModelType =
                getViewModelTypeForEnsuredElementWrapper(world, viewModel.getType().getName().value)
            val wrappedViewType = mapper.mapViewModelToViewType(wrappedViewModelType)
            viewExtends = WorldType.create(
                WorldTypeName("${wrapperName}<${wrappedViewType.getName()}>"),
                view.getPath()
            )
            world.addConcreteParametrizedClass(
                WorldConcreteParametrizedClass.create(
                    type = viewExtends,
                    typeArguments = listOf(wrappedViewType)
                )
            )
        }
        return viewExtends
    }
}