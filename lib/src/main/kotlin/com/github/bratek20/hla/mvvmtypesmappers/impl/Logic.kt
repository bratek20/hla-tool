package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.mvvmtypesmappers.api.*
import com.github.bratek20.hla.mvvmtypesmappers.impl.BaseViewModelTypesMapper.Companion.b20ViewModelTypes

import com.github.bratek20.hla.typesworld.api.*

class ViewModelToViewMapperLogic(
    private val typesWorldApi: TypesWorldApi
): ViewModelToViewMapper {
    override fun map(viewModel: WorldType): WorldType {
        val viewModelType = viewModel.getName().value
        if (b20ViewModelTypes.contains(viewModelType)) {
            return WorldType.create(
                name = WorldTypeName(viewModelType + "View"),
                path = WorldTypePath("B20/View/UiElements")
            )
        }

        val viewTypeName = mapViewModelToViewTypeName(viewModel)
        return WorldType.create(
            name = WorldTypeName(viewTypeName),
            path = viewModel.getPath().asHla()
                .replaceSubmoduleAndPattern(SubmoduleName.View, PatternName.ElementsView)
                .asWorld()
        )
    }

    private fun mapViewModelToViewTypeName(viewModel: WorldType): String {
        val viewModelType = viewModel.getName().value

        if (viewModelType.endsWith("SwitchGroup")) {
            return viewModelType + "View"
        }

        if (viewModelType.endsWith("Group")) {
            return getModelTypeForEnsuredUiElement(typesWorldApi, viewModelType).getName().value + "GroupView"
        }
        if (viewModelType.startsWith("Optional")) {
            val wrappedTypeName = viewModelType.replace("Optional", "")
            return "Optional" + mapViewModelToViewTypeName(typesWorldApi.getTypeByName(WorldTypeName(wrappedTypeName)))
        }
        if (viewModelType.endsWith("Window") || viewModelType.endsWith("Popup")) {
            return viewModelType + "View"
        }

        val modelType = getModelTypeForEnsuredUiElement(typesWorldApi, viewModelType)
        if (modelType.getName().value == "EmptyModel") {
            return viewModelType + "View"
        }
        if (viewModelType.endsWith("Switch")) {
            return modelType.getName().value + "SwitchView"
        }

        if (viewModelType.endsWith("Vm")) {
            return viewModelType.dropLast(2) + "View"
        }
        return viewModelType + "View"
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