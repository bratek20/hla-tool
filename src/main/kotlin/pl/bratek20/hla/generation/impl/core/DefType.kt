package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.utils.pascalToCamelCase

interface DefType {
    fun toView(): String

    fun defaultValue(): String

    fun constructor(x: String): String
}

data class BaseDefType(
    val domain: BaseViewType,
    val languageTypes: LanguageTypes
) : DefType {
    override fun toView(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }

    override fun constructor(x: String): String {
        return x
    }
}

data class SimpleVODefType(
    val domain: SimpleVOViewType,
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
    private val languageTypes: LanguageTypes
) {
    fun create(type: ViewType): DefType {
        return when (type) {
            is BaseViewType -> BaseDefType(type, languageTypes)
            is SimpleVOViewType -> SimpleVODefType(type, create(type.boxedType) as BaseDefType)
            is ComplexVOViewType -> ComplexVODefType(type.name)
            is ListViewType -> ListDefType(create(type.wrappedType))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
