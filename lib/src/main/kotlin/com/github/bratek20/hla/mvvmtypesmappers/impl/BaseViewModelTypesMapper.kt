package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

open class BaseViewModelTypesMapper {
    fun mapModelToViewModelTypeName(modelType: LegacyApiType): String {
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
        return "TODO-mapModelToViewModelTypeName-${modelType.name()}"
    }

    fun mapModelToViewModelType(modelType: LegacyApiType): WorldType {
        val viewModelTypeName = mapModelToViewModelTypeName(modelType)
        if (ModelToViewModelTypeMapper.b20ViewModelTypes.contains(viewModelTypeName)) {
            return WorldType.create(
                name = WorldTypeName(viewModelTypeName),
                path = B20FrontendTypesPopulator.labelType.getPath()
            )
        }
        return WorldType.create(
            name = WorldTypeName(viewModelTypeName),
            path = modelType.asWorldType().getPath().asHla()
                .replaceSubmoduleAndPattern(SubmoduleName.ViewModel, PatternName.GeneratedElements)
                .asWorld()
        )
    }

    private fun mapComplexStructureType(modelType: ComplexStructureApiType<*>): String {
        ////TODO-FIX this is hack based on current name assumptions
        return modelType.name + "Vm"
    }

    private fun mapListType(modelType: ListApiType): String {
        if(modelType.wrappedType is ComplexStructureApiType<*>) {
            val x = mapComplexStructureType(modelType.wrappedType)
            return x + "Group"
        }
        if(modelType.wrappedType is EnumApiType) {
            return mapModelToViewModelTypeName(modelType.wrappedType) + "Group"
        }
        throw IllegalArgumentException("Unsupported mapListType for: ${modelType.wrappedType}")
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