package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

fun getModelTypeForEnsuredUiElement(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[0]
}

fun getModelTypeForEnsuredUiElementGroup(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[1]
}

fun getViewModelTypeForEnsuredUiElementGroup(typesWorldApi: TypesWorldApi, viewModelType: String): WorldType {
    val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
    val classType = typesWorldApi.getClassType(type)
    return typesWorldApi.getConcreteParametrizedClass(classType.getExtends()!!).getTypeArguments()[0]
}

