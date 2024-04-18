package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.utils.pascalToCamelCase

interface DefViewType {
    fun name(): String

    fun defaultValue(): String

    fun constructor(x: String): String
}

data class BaseDefViewType(
    val domain: BaseViewType,
    val languageTypes: LanguageTypes
) : DefViewType {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }

    override fun constructor(x: String): String {
        return x
    }
}

data class SimpleVODefViewType(
    val domain: SimpleVOViewType,
    val boxedType: BaseDefViewType
) : DefViewType {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun constructor(x: String): String {
        return "${domain.name}($x)"
    }
}

data class ComplexVODefViewType(
    val name: String
) : DefViewType {
    override fun name(): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(x: String): String {
        return "${pascalToCamelCase(name)}($x)"
    }
}

data class ListDefViewType(
    val wrappedType: DefViewType
) : DefViewType {
    override fun name(): String {
        return "List<${wrappedType.name()}>"
    }

    override fun defaultValue(): String {
        return "emptyList()"
    }

    override fun constructor(x: String): String {
        if (wrappedType is BaseDefViewType) {
            return x
        }
        return "$x.map { ${wrappedType.constructor("it")} }"
    }
}

class DefTypeFactory(
    private val languageTypes: LanguageTypes
) {
    fun create(type: ViewType): DefViewType {
        return when (type) {
            is BaseViewType -> BaseDefViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVODefViewType(type, create(type.boxedType) as BaseDefViewType)
            is ComplexVOViewType -> ComplexVODefViewType(type.name)
            is ListViewType -> ListDefViewType(create(type.wrappedType))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
