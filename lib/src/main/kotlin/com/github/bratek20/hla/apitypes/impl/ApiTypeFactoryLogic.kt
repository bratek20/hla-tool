package com.github.bratek20.hla.apitypes.impl

import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.impl.core.api.ComplexStructureField
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.queries.api.isBaseType
import com.github.bratek20.hla.queries.api.ofBaseType

class ApiTypeFactoryLogic(
    val modules: BaseModuleGroupQueries,
    val languageTypes: LanguageTypes
): ApiTypeFactory {
    override fun create(type: TypeDefinition?): ApiTypeLogic {
        if (type == null) {
            return createBaseApiType(BaseType.VOID)
        }

        val simpleVO = modules.findSimpleValueObject(type)
        val complexVO = modules.findComplexValueObject(type)
        val isOptional = type.getWrappers().contains(TypeWrapper.OPTIONAL)
        val isList = type.getWrappers().contains(TypeWrapper.LIST)
        val isBaseType = isBaseType(type.getName())
        val enum = modules.findEnum(type)
        val simpleCustomType = modules.findSimpleCustomType(type)
        val complexCustomType = modules.findComplexCustomType(type)
        val dataVO = modules.findDataClass(type)
        val interf = modules.findInterface(type)
        val externalTypeName = modules.findExternalType(type)
        val event = modules.findEvent(type)

        val apiType = when {
            isOptional -> OptionalApiType(create(withoutTypeWrapper(type, TypeWrapper.OPTIONAL)))
            isList -> ListApiType(create(withoutTypeWrapper(type, TypeWrapper.LIST)))
            simpleVO != null -> SimpleValueObjectApiType(
                simpleVO,
                createBaseApiType(ofBaseType(simpleVO.getTypeName()))
            )
            simpleCustomType != null -> SimpleCustomApiType(
                simpleCustomType,
                createBaseApiType(ofBaseType(simpleCustomType.getTypeName()))
            )
            complexVO != null -> ComplexValueObjectApiType(type.getName(), createComplexStructureFields(complexVO))
            dataVO != null -> DataClassApiType(type.getName(), createComplexStructureFields(dataVO))
            complexCustomType != null -> ComplexCustomApiType(
                type.getName(),
                createComplexStructureFields(complexCustomType)
            )
            isBaseType -> BaseApiType(ofBaseType(type.getName()))
            enum != null -> EnumApiType(enum)
            interf != null -> InterfaceApiType(type.getName())
            externalTypeName != null -> ExternalApiType(externalTypeName)
            event != null -> EventApiType(type.getName(), createComplexStructureFields(event))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        apiType.init(languageTypes, modules.findTypeModule(type.getName()))

        if (apiType is ComplexStructureApiType<*>) {
            apiType.fields.forEach { it.init(apiType) }
        }

        return apiType
    }

    private fun withoutTypeWrapper(type: TypeDefinition, wrapper: TypeWrapper): TypeDefinition {
        val finalWrappers = type.getWrappers() - wrapper
        return TypeDefinition.create(
            type.getName(),
            finalWrappers
        )
    }

    inline fun <reified T: SimpleStructureApiType> create(def: SimpleStructureDefinition): T {
        return create(TypeDefinition(def.getName(), emptyList())) as T
    }

    inline fun <reified T: ComplexStructureApiType<*>> create(def: ComplexStructureDefinition): T {
        return create(TypeDefinition(def.getName(), emptyList())) as T
    }

    private fun createBaseApiType(type: BaseType): BaseApiType {
        val result = BaseApiType(type)
        result.init(languageTypes, null)
        return result
    }

    private fun createComplexStructureFields(def: ComplexStructureDefinition): List<ComplexStructureField> {
        return def.getFields().map {
            ComplexStructureField(it, this)
        }
    }
}