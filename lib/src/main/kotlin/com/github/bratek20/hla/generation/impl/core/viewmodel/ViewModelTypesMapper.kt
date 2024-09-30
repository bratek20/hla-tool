package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.*

class ModelToViewModelTypeMapper(
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

    fun mapViewModelWrappedTypeToListType(viewModelType: String): String {
        val modelType = mapViewModelToModelType(viewModelType)
        return mapModelToViewModelTypeName(ListApiType(modelType))
    }

    fun mapViewModelWrappedTypeToOptionalType(viewModelType: String): String {
        val modelType = mapViewModelToModelType(viewModelType)
        return mapModelToViewModelTypeName(OptionalApiType(modelType))
    }

    fun mapViewModelToModelType(viewModelType: String): ApiType {
        return viewModelElements.first { it.getTypeName() == viewModelType }.modelType
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
            return "EnumSwitch"
        }
        if (modelType is ListApiType) {
            return mapListType(modelType)
        }
        if (modelType is OptionalApiType) {
            return mapOptionalType(modelType)
        }
        return "TODO"
    }

    fun getModelForViewModelType(viewModelType: String): ComplexStructureApiType<*> {
        return viewModelElements.first { it.getTypeName() == viewModelType }.modelType
    }

    private fun getViewModelElementForType(modelType: ComplexStructureApiType<*>): ViewModelElementLogic {
        return viewModelElements.first { it.modelType.name == modelType.name }
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
}