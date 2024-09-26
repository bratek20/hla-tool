package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.*

class ModelToViewModelTypeMapper(
    private val viewModelElements: List<ViewModelElementLogic>
) {
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
        if(modelType.wrappedType is ComplexStructureApiType<*>) {
            val x = mapComplexStructureType(modelType.wrappedType)
            return "Optional" + x
        }
        return "TODO"
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