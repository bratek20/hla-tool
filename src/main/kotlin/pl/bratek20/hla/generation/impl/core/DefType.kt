package pl.bratek20.hla.generation.impl.core

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
