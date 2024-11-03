package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.type.api.HlaType
import com.github.bratek20.hla.type.api.HlaTypePath

class ModelToViewModelTypeMapper(
    private val apiTypeFactory: ApiTypeFactory,
    private val viewModelElements: List<ViewModelElementLogic>
) {
    fun mapViewModelToViewTypeName(viewModelType: String): String {
        val knownViewModel = viewModelElements.firstOrNull { it.getTypeName() == viewModelType }
        if (knownViewModel != null) {
            return mapModelToViewTypeName(knownViewModel.modelType)
        }
        if (viewModelType.endsWith("Group")) {
            val wrappedTypeName = viewModelType.replace("Group", "")
            return mapViewModelToModelType(wrappedTypeName).name() + "GroupView"
        }
        if (viewModelType.startsWith("Optional")) {
            val wrappedTypeName = viewModelType.replace("Optional", "")
            return "Optional" + mapViewModelToViewTypeName(wrappedTypeName)
        }
        return viewModelType + "View"
    }

    fun mapViewModelToViewType(viewModelType: HlaType): HlaType {
        val viewModelTypeName = viewModelType.getName()
        val viewTypeName = mapViewModelToViewTypeName(viewModelTypeName)
        return HlaType.create(
            name = viewTypeName,
            path = viewModelType.getPath()
        )
    }

    fun mapViewModelWrappedTypeToListType(viewModelType: String): String {
        val listApiType = mapViewModelWrappedTypeToListApiType(viewModelType)
        return mapModelToViewModelTypeName(listApiType)
    }

    fun mapViewModelWrappedTypeToListApiType(viewModelType: String): ListApiType {
        val modelType = mapViewModelToModelType(viewModelType)
        val listApiType = apiTypeFactory.create(TypeDefinition.create(
            name = modelType.name(),
            wrappers = listOf(TypeWrapper.LIST)
        ))
        return listApiType as ListApiType
    }

    fun mapViewModelWrappedTypeToOptionalType(viewModelType: String): String {
        val optionalApiType = mapViewModelWrappedTypeToOptionalApiType(viewModelType)
        return mapModelToViewModelTypeName(optionalApiType)
    }

    fun mapViewModelWrappedTypeToOptionalApiType(viewModelType: String): OptionalApiType {
        val modelType = mapViewModelToModelType(viewModelType)
        val optionalApiType = apiTypeFactory.create(TypeDefinition.create(
            name = modelType.name(),
            wrappers = listOf(TypeWrapper.OPTIONAL)
        ))
        return optionalApiType as OptionalApiType
    }

    fun mapViewModelToModelType(viewModelType: String): ApiType {
        return viewModelElements.firstOrNull() { it.getTypeName() == viewModelType }?.modelType
            ?: throw IllegalArgumentException("Unknown view model type: $viewModelType")
    }

    fun mapModelToViewTypeName(modelType: ApiType): String {
        if (modelType is ListApiType) {
            return mapModelToViewTypeName(modelType.wrappedType).replace("View", "GroupView")
        }
        if (modelType is OptionalApiType) {
            return "Optional" + mapModelToViewTypeName(modelType.wrappedType)
        }

        var baseName = mapModelToViewModelTypeName(modelType)
        if (modelType is ComplexStructureApiType<*>) {
            baseName = modelType.name
        }
        return baseName + "View"
    }

    fun mapModelToViewType(modelType: ApiType): HlaType {
        val viewTypeName = mapModelToViewTypeName(modelType)
        return HlaType.create(
            name = viewTypeName,
            path = modelType.asHlaType().getPath()
                .replaceSubmodule(SubmoduleName.View)
        )
    }

    fun mapModelToViewModelTypeName(modelType: ApiType): String {
        if (modelType is BaseApiType) {
            return mapBaseType(modelType)
        }
        if (modelType is SimpleStructureApiType) {
            return mapBaseType(modelType.boxedType)
        }
        if (modelType is ComplexStructureApiType<*>) {
            return mapComplexStructureType(modelType)
        }
        if (modelType is EnumApiType) {
            return modelType.name() + "Switch"
        }
        if (modelType is ListApiType) {
            return mapListType(modelType)
        }
        if (modelType is OptionalApiType) {
            return mapOptionalType(modelType)
        }
        return "TODO"
    }

    fun getModelForViewModelType(viewModelType: String): ApiType {
        return viewModelElements.first { it.getTypeName() == viewModelType }.modelType
    }

    private fun getViewModelElementForType(modelType: ApiType): ViewModelElementLogic {
        return viewModelElements.first { it.modelType.name() == modelType.name() }
    }

    private fun mapComplexStructureType(modelType: ComplexStructureApiType<*>): String {
        return getViewModelElementForType(modelType).getTypeName()
    }

    private fun mapListType(modelType: ListApiType): String {
        if(modelType.wrappedType is ComplexStructureApiType<*>) {
            val x = mapComplexStructureType(modelType.wrappedType)
            return x + "Group"
        }
        return "TODO"
    }

    private fun mapOptionalType(modelType: OptionalApiType): String {
        return "Optional" + mapModelToViewModelTypeName(modelType.wrappedType)
    }

    private fun mapBaseType(type: BaseApiType): String {
        return when (type.name) {
            BaseType.STRING -> "Label"
            BaseType.INT -> "Label"
            BaseType.BOOL -> "BoolSwitch"
            BaseType.DOUBLE -> "Label"
            BaseType.LONG -> "Label"
            BaseType.STRUCT -> throw IllegalArgumentException("Structs are not supported in view models")
            BaseType.VOID -> throw IllegalArgumentException("Void is not supported in view models")
            BaseType.ANY -> throw IllegalArgumentException("Any is not supported in view models")
        }
    }

    fun mapViewModelToFullViewTypeName(viewModelTypeName: String): String {
        val viewType = mapViewModelToViewTypeName(viewModelTypeName)
        if (b20ViewTypes.contains(viewType)) {
            return "B20.Frontend.Elements.View.$viewType"
        }
        return "${getViewModelModuleName(viewModelTypeName)}.View.$viewType"
    }

    private fun getViewModelModuleName(viewModelType: String): String {
        if (viewModelType.endsWith("Group")) {
            val wrappedTypeName = viewModelType.replace("Group", "")
            return getViewModelModuleName(wrappedTypeName)
        }
        if (viewModelType.startsWith("Optional")) {
            val wrappedTypeName = viewModelType.replace("Optional", "")
            return getViewModelModuleName(wrappedTypeName)
        }

        val apiType = mapViewModelToModelType(viewModelType)
        return apiType.moduleName()
    }

    companion object {
        val b20ViewTypes = listOf(
            "LabelView",
            "LabelGroupView",
            "OptionalLabelView",
            "ButtonView",
            "BoolSwitchView",
        )
    }
}