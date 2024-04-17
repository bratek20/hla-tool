package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import pl.bratek20.hla.model.*
import pl.bratek20.hla.utils.pascalToCamelCase

interface DefType {
    fun toView(): String

    fun defaultValue(): String

    fun constructor(x: String): String
}

data class BaseDefType(
    val domain: BaseDomainType,
    val types: Types
) : DefType {
    override fun toView(): String {
        return domain.toView()
    }

    override fun defaultValue(): String {
        return types.defaultValueForBaseType(domain.name)
    }

    override fun constructor(x: String): String {
        return x
    }
}

data class SimpleVODefType(
    val domain: SimpleVODomainType,
    val boxedType: BaseDefType
) : DefType {
    override fun toView(): String {
        return boxedType.toView()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun constructor(x: String): String {
        return "${domain.name}($x)"
    }
}

data class ComplexVODefType(
    val name: String
) : DefType {
    override fun toView(): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(x: String): String {
        return "${pascalToCamelCase(name)}($x)"
    }
}

data class ListDefType(
    val wrappedType: DefType
) : DefType {
    override fun toView(): String {
        return "List<${wrappedType.toView()}>"
    }

    override fun defaultValue(): String {
        return "emptyList()"
    }

    override fun constructor(x: String): String {
        if (wrappedType is BaseDefType) {
            return x
        }
        return "$x.map { ${wrappedType.constructor("it")} }"
    }
}

interface DomainType {
    fun toView(): String
}

data class BaseDomainType(
    val name: String,
    val types: Types
) : DomainType {
    override fun toView(): String {
        return types.mapBaseType(name)
    }
}

data class SimpleVODomainType(
    val name: String,
    val boxedType: BaseDomainType
) : DomainType {
    override fun toView(): String {
        return boxedType.toView()
    }
}


data class ComplexVODomainType(
    val name: String,
) : DomainType {
    override fun toView(): String {
        return name
    }
}

data class ListDomainType(
    val wrappedType: DomainType,
    val types: Types
) : DomainType {
    override fun toView(): String {
        return types.wrapWithList(wrappedType.toView())
    }
}

class DefTypeFactory(
    private val types: Types
) {
    fun create(type: DomainType): DefType {
        return when (type) {
            is BaseDomainType -> BaseDefType(type, types)
            is SimpleVODomainType -> SimpleVODefType(type, create(type.boxedType) as BaseDefType)
            is ComplexVODomainType -> ComplexVODefType(type.name)
            is ListDomainType -> ListDefType(create(type.wrappedType))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}

class DomainFactory(
    private val module: HlaModule,
    private val types: Types
) {
    fun mapOptType(type: Type?): DomainType? {
        if (type == null) {
            return null
        }
        return mapType(type)
    }

    fun mapType(type: Type): DomainType {
        val simpleVO = findSimpleVO(type)
        val complexVO = findComplexVO(type)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        return when {
            isList -> ListDomainType(mapType(type.copy(wrappers = type.wrappers - TypeWrapper.LIST)), types)
            simpleVO != null -> SimpleVODomainType(type.name, BaseDomainType(simpleVO.typeName, types))
            complexVO != null -> ComplexVODomainType(type.name)
            else -> BaseDomainType(type.name, types)
        }
    }


    private fun findSimpleVO(type: Type): SimpleValueObject? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: Type): ComplexValueObject? {
        return module.complexValueObjects.find { it.name == type.name }
    }
}