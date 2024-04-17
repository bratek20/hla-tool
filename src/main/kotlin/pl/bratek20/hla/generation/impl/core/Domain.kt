package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.model.*

enum class TypeKind {
    BASE,
    SIMPLE_VO,
    COMPLEX_VO
}

data class OldDomainType(
    val name: String,
    val kind: TypeKind,
    val boxedTypeName: String? = null,
    val isList: Boolean = false,
) {
    val isBoxed: Boolean
        get() = boxedTypeName != null

    fun unbox(): OldDomainType {
        if (!isBoxed) {
            return this
        }
        return OldDomainType(
            name = boxedTypeName!!,
            kind = TypeKind.BASE,
            boxedTypeName = null,
            isList = isList
        )
    }
}

class OldDomainFactory(
    private val module: HlaModule
) {
    fun mapOptType(type: Type?): OldDomainType? {
        if (type == null) {
            return null
        }
        return mapType(type)
    }

    fun mapType(type: Type): OldDomainType {
        val simpleVO = findSimpleVO(type)
        return OldDomainType(
            name = type.name,
            kind = when {
                simpleVO != null -> TypeKind.SIMPLE_VO
                findComplexVO(type) != null -> TypeKind.COMPLEX_VO
                else -> TypeKind.BASE
            },
            boxedTypeName = simpleVO?.typeName,
            isList = type.wrappers.contains(TypeWrapper.LIST)
        )
    }


    private fun findSimpleVO(type: Type): SimpleValueObject? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: Type): ComplexValueObject? {
        return module.complexValueObjects.find { it.name == type.name }
    }
}