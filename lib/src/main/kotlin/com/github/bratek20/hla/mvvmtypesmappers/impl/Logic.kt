package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator.Companion.b20ViewModelTypes
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelToViewMapper
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelTypesCalculator
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.api.WorldTypePath

class ViewModelToViewMapperLogic(
    private val typesWorldApi: TypesWorldApi
): ViewModelToViewMapper {
    override fun map(viewModel: WorldType): WorldType {
        val viewModelType = viewModel.getName().value
        if (b20ViewModelTypes.contains(viewModelType)) {
            return WorldType.create(
                name = WorldTypeName(viewModelType + "View"),
                path = HlaTypePath.create(
                    listOf(GroupName("B20"), GroupName("View")),
                    ModuleName("UiElements"),
                    SubmoduleName.View,
                    PatternName.Undefined
                ).asWorld()
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