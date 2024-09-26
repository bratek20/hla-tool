package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.ApiType
import com.github.bratek20.hla.generation.impl.core.api.BaseApiType
import com.github.bratek20.hla.generation.impl.core.api.SimpleStructureApiType

fun mapModelTypeToViewModelType(modelType: ApiType): TypeBuilder {
    if (modelType is BaseApiType) {
        return mapBaseApiType(modelType)
    }
    if (modelType is SimpleStructureApiType) {
        return mapBaseApiType(modelType.boxedType)
    }
    return typeName("TODO")
}

private fun mapBaseApiType(type: BaseApiType): TypeBuilder {
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