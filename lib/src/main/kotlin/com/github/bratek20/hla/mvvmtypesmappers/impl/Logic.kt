package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.mvvmtypesmappers.api.*

import com.github.bratek20.hla.typesworld.api.*

class ViewModelToViewMapperLogic(
    private val typesWorldApi: TypesWorldApi
): ViewModelToViewMapper {
    override fun map(viewModel: WorldType): WorldType {
        val viewModelTypeName = viewModel.getName().value
        val viewTypeName = mapViewModelToViewTypeName(viewModelTypeName)
        return WorldType.create(
            name = WorldTypeName(viewTypeName),
            path = viewModel.getPath().asHla()
                .replaceSubmoduleAndPattern(SubmoduleName.View, PatternName.ElementsView)
                .asWorld()
        )
    }

    fun mapViewModelToViewTypeName(viewModelType: String): String {
        if (ModelToViewModelTypeMapper.b20ViewModelTypes.contains(viewModelType)) {
            return viewModelType + "View"
        }

        if (viewModelType.endsWith("SwitchGroup")) {
            return viewModelType + "View"
        }

        if (viewModelType.endsWith("Group")) {
            return getModelTypeForEnsuredUiElementGroup(typesWorldApi, viewModelType).getName().value + "GroupView"
        }
        if (viewModelType.startsWith("Optional")) {
            val wrappedTypeName = viewModelType.replace("Optional", "")
            return "Optional" + mapViewModelToViewTypeName(wrappedTypeName)
        }
        if (viewModelType.endsWith("Window")) {
            return viewModelType + "View"
        }

        val modelType = getModelTypeForEnsuredUiElement(typesWorldApi, viewModelType)
        if (modelType.getName().value == "EmptyModel") {
            return viewModelType + "View"
        }
        if (viewModelType.endsWith("Switch")) {
            return modelType.getName().value + "SwitchView"
        }
        return modelType.getName().value + "View"
    }
}

class ViewModelTypesCalculatorLogic: ViewModelTypesCalculator {
    override fun wrapWithGroup(viewModel: WorldType): WorldType {
        return WorldType.create(
            name = WorldTypeName(viewModel.getName().value + "Group"),
            path = viewModel.getPath()
        )
    }

    override fun wrapWithOptional(viewModel: WorldType): WorldType {
        return WorldType.create(
            name = WorldTypeName("Optional" + viewModel.getName().value),
            path = viewModel.getPath()
        )
    }
}