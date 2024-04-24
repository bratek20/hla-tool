package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.model.BaseType
import pl.bratek20.hla.model.Type
import pl.bratek20.hla.model.TypeWrapper

interface DomainType

data class BaseDomainType(
    val name: BaseType
) : DomainType

data class SimpleVODomainType(
    val name: String,
    val boxedType: BaseDomainType
) : DomainType


data class ComplexVODomainType(
    val name: String
) : DomainType

data class ListDomainType(
    val wrappedType: DomainType
) : DomainType

class DomainTypeFactory(
    private val modules: HlaModules
) {

    fun create(type: Type?): DomainType {
        if (type == null) {
            return BaseDomainType(BaseType.VOID)
        }

        val simpleVO = modules.findSimpleVO(type)
        val complexVO = modules.findComplexVO(type)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        val isBaseType = BaseType.isBaseType(type.name)
        
        return when {
            isList -> ListDomainType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST)))
            simpleVO != null -> SimpleVODomainType(type.name, BaseDomainType(BaseType.of(simpleVO.typeName)))
            complexVO != null -> ComplexVODomainType(type.name)
            isBaseType -> BaseDomainType(BaseType.of(type.name))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}