package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeMapping
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.*

class ModelToViewModelTypeMapper {
    fun map(modelType: ApiType): TypeBuilder {
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
            return typeName("EnumSwitch")
        }
        if (modelType is ListApiType) {
            return mapListType(modelType)
        }
        return typeName("TODO")
    }

    private fun mapComplexStructureType(modelType: ComplexStructureApiType<*>): TypeBuilder {
        return typeName("SomeClass2Vm") //TODO-GENERALIZE
    }

    private fun mapListType(modelType: ListApiType): TypeBuilder {
        if(modelType.wrappedType is ComplexStructureApiType<*>) {
            val x = mapComplexStructureType(modelType.wrappedType)
            return typeMapping(x) { "${it}Group" }
        }
        return typeName("TODO")
    }

    private fun mapBaseType(type: BaseApiType): TypeBuilder {
        return when (type.name) {
            BaseType.STRING -> typeName("Label")
            BaseType.INT -> typeName("Label")
            BaseType.BOOL -> typeName("BoolSwitch")
            BaseType.DOUBLE -> typeName("Label")
            BaseType.LONG -> typeName("Label")
            BaseType.STRUCT -> throw IllegalArgumentException("Structs are not supported in view models")
            BaseType.VOID -> throw IllegalArgumentException("Void is not supported in view models")
            BaseType.ANY -> throw IllegalArgumentException("Any is not supported in view models")
        }
    }
}