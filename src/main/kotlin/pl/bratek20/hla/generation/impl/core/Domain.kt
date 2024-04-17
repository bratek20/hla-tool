package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.model.Type
import pl.bratek20.hla.model.TypeWrapper

data class DomainType(
    val name: String,
    val baseName: String? = null,
    val isList: Boolean = false,
) {
    val isWrapped: Boolean
        get() = baseName != null

    fun unwrap(): DomainType {
        if (!isWrapped) {
            return this
        }
        return DomainType(
            name = baseName!!,
            baseName = null,
            isList = isList
        )
    }
}

class DomainFactory(
    private val module: HlaModule
) {
    fun mapOptType(type: Type?): DomainType? {
        if (type == null) {
            return null
        }
        return mapType(type)
    }

    fun mapType(type: Type): DomainType {
        val simpleVO = findSimpleVO(type)
        return DomainType(
            name = type.name,
            baseName = simpleVO?.typeName,
            isList = type.wrappers.contains(TypeWrapper.LIST)
        )
    }


    private fun findSimpleVO(type: Type): SimpleValueObject? {
        return module.simpleValueObjects.find { it.name == type.name }
    }
}