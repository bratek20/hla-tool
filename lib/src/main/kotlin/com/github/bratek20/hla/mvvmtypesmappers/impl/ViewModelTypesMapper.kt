package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

fun getModelTypeForEnsuredUiElement(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    if (viewModelType.endsWith("Group") || viewModelType.startsWith("Optional")) {
        return getModelTypeForEnsuredUiElementWrapper(typesWorldApi, viewModelType)
    }
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[0]
}

private fun getModelTypeForEnsuredUiElementWrapper(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[1]
}

fun getViewModelTypeForEnsuredElementWrapper(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[0]
}

