package com.github.bratek20.hla.mvvmtypesmappers.impl

import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.queries.api.createTypeDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

class ModelToViewModelTypeMapper(
    private val apiTypeFactory: ApiTypeFactoryLogic,
    val typesWorldApi: TypesWorldApi
): BaseViewModelTypesMapper() {
    private val vmToViewMapper = ViewModelToViewMapperLogic(typesWorldApi)

    fun mapViewModelToViewTypeName(viewModelType: String): String {
        return vmToViewMapper.mapViewModelToViewTypeName(viewModelType)
    }

    fun mapViewModelToViewType(viewModelType: WorldType): WorldType {
        return vmToViewMapper.map(viewModelType)
    }

    fun mapViewModelNameToViewType(viewModelTypeName: String): WorldType {
        val viewModelType = typesWorldApi.getTypeByName(WorldTypeName(viewModelTypeName))
        return mapViewModelToViewType(viewModelType)
    }

    fun mapViewModelWrappedTypeToListType(viewModelType: String): String {
        val listApiType = mapViewModelWrappedTypeToListApiType(viewModelType)
        return mapModelToViewModelTypeName(listApiType)
    }

    private fun mapViewModelWrappedTypeToListApiType(viewModelType: String): ListApiType {
        val modelType = mapViewModelToModelType(viewModelType)
        val listApiType = apiTypeFactory.create(
            TypeDefinition.create(
                name = modelType.name(),
                wrappers = listOf(TypeWrapper.LIST)
            )
        )
        return listApiType as ListApiType
    }

    fun mapViewModelWrappedTypeToOptionalType(viewModelType: String): String {
        val optionalApiType = mapViewModelWrappedTypeToOptionalApiType(viewModelType)
        return mapModelToViewModelTypeName(optionalApiType)
    }

    fun mapViewModelWrappedTypeToOptionalApiType(viewModelType: String): OptionalApiType {
        val modelType = mapViewModelToModelType(viewModelType)
        val optionalApiType = apiTypeFactory.create(
            TypeDefinition.create(
                name = modelType.name(),
                wrappers = listOf(TypeWrapper.OPTIONAL)
            )
        )
        return optionalApiType as OptionalApiType
    }

    fun mapViewModelToModelType(viewModelType: String): ApiTypeLogic {
        val classType = typesWorldApi.getClassType(typesWorldApi.getTypeByName(WorldTypeName(viewModelType)))
        val extendedClass = classType.getExtends()!!

        val modelType = typesWorldApi.getConcreteParametrizedClass(extendedClass).getTypeArguments()[0]
        return apiTypeFactory.create(createTypeDefinition(modelType.getName().value))
    }

    fun mapModelToViewTypeName(modelType: ApiTypeLogic): String {
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


    fun mapModelToViewType(modelType: ApiTypeLogic): WorldType {
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