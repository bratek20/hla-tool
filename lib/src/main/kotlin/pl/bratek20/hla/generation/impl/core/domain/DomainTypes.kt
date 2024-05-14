package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.definitions.api.EnumDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.definitions.api.TypeWrapper
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.definitions.impl.isBaseType
import pl.bratek20.hla.definitions.impl.ofBaseType

interface DomainType

data class BaseDomainType(
    val name: BaseType
) : DomainType

data class SimpleVODomainType(
    val name: String,
    val boxedType: BaseDomainType
) : DomainType

data class SimpleCustomDomainType(
    val name: String,
    val boxedType: BaseDomainType
) : DomainType

data class ComplexVODomainType(
    val name: String
) : DomainType

data class ComplexCustomDomainType(
    val name: String
) : DomainType

data class PropertyVODomainType(
    val name: String
) : DomainType

data class ListDomainType(
    val wrappedType: DomainType
) : DomainType

data class EnumDomainType(
    private val def: EnumDefinition
) : DomainType {
    val name = def.name
    val values = def.values

    fun defaultValue(): String {
        return values.first()
    }
}

class DomainTypeFactory(
    private val modules: HlaModules
) {

    fun create(type: TypeDefinition?): DomainType {
        if (type == null) {
            return BaseDomainType(BaseType.VOID)
        }

        val simpleVO = modules.findSimpleVO(type)
        val complexVO = modules.findComplexVO(type)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        val isBaseType = isBaseType(type.name)
        val enum = modules.findEnum(type)
        val propertyVO = modules.findPropertyVO(type)
        val simpleCustomType = modules.findSimpleCustomType(type)
        val complexCustomType = modules.findComplexCustomType(type)
        
        return when {
            isList -> ListDomainType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST)))
            simpleVO != null -> SimpleVODomainType(type.name, BaseDomainType(ofBaseType(simpleVO.typeName)))
            complexVO != null -> ComplexVODomainType(type.name)
            isBaseType -> BaseDomainType(ofBaseType(type.name))
            enum != null -> EnumDomainType(enum)
            propertyVO != null -> PropertyVODomainType(type.name)
            simpleCustomType != null -> SimpleCustomDomainType(type.name, BaseDomainType(ofBaseType(simpleCustomType.typeName)))
            complexCustomType != null -> ComplexCustomDomainType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}