package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

open class BaseViewModelTypesMapper {
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
        return "TODO-mapModelToViewModelTypeName-${modelType.name()}"
    }

    fun mapModelToViewModelType(modelType: ApiType): WorldType {
        val viewModelTypeName = mapModelToViewModelTypeName(modelType)
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

class ModelToViewModelTypeMapper(
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
): BaseViewModelTypesMapper() {
    fun mapViewModelToViewTypeName(viewModelType: String): String {
        if(b20ViewModelTypes.contains(viewModelType)) {
            return viewModelType + "View"
        }

        if (viewModelType.endsWith("Group")) {
            val wrappedTypeName = viewModelType.replace("Group", "")
            return mapViewModelToModelType(wrappedTypeName).name() + "GroupView"
        }
        if (viewModelType.startsWith("Optional")) {
            val wrappedTypeName = viewModelType.replace("Optional", "")
            return "Optional" + mapViewModelToViewTypeName(wrappedTypeName)
        }

        val modelType = getModelTypeForEnsuredUiElement(typesWorldApi, viewModelType)
        if (modelType.getName().value == "EmptyModel") {
            return viewModelType + "View"
        }
        if(viewModelType.endsWith("Switch")) {
            return modelType.getName().value + "SwitchView"
        }
        return modelType.getName().value + "View"
    }

    fun mapViewModelToViewType(viewModelType: WorldType): WorldType {
        val viewModelTypeName = viewModelType.getName().value
        val viewTypeName = mapViewModelToViewTypeName(viewModelTypeName)
        return WorldType.create(
            name = WorldTypeName(viewTypeName),
            path = viewModelType.getPath().asHla()
                .replaceSubmoduleAndPattern(SubmoduleName.View, PatternName.ElementsView)
                .asWorld()
        )
    }

    fun mapViewModelNameToViewType(viewModelTypeName: String): WorldType {
        val viewModelType = typesWorldApi.getTypeByName(WorldTypeName(viewModelTypeName))
        return mapViewModelToViewType(viewModelType)
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
        val classType = typesWorldApi.getClassType(typesWorldApi.getTypeByName(WorldTypeName(viewModelType)))
        val extendedClass = classType.getExtends()!!

        val modelType = typesWorldApi.getConcreteParametrizedClass(extendedClass).getTypeArguments()[0]
        return apiTypeFactory.create(createTypeDefinition(modelType.getName().value))
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


    fun mapModelToViewType(modelType: ApiType): WorldType {
        val viewTypeName = mapModelToViewTypeName(modelType)
        return WorldType.create(
            name = WorldTypeName(viewTypeName),
            path = modelType.asWorldType().getPath().asHla()
                .replaceSubmoduleAndPattern(SubmoduleName.View, PatternName.ElementsView)
                .asWorld()
        )
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

        val type = typesWorldApi.getTypeByName(WorldTypeName(viewModelType))
        return type.getPath().asHla().getModuleName().value
    }

    companion object {
        val b20ViewModelTypes = listOf(
            "Label",
            "LabelGroup",
            "OptionalLabel",
            "Button",
            "BoolSwitch",
        )
        val b20ViewTypes = listOf(
            "LabelView",
            "LabelGroupView",
            "OptionalLabelView",
            "ButtonView",
            "BoolSwitchView",
        )
    }
}