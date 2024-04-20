package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.generation.impl.core.domain.*

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
    val boxedType: BaseDefViewType,
    val languageTypes: LanguageTypes
) : DefViewType {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun constructor(x: String): String {
        return languageTypes.classConstructor(domain.name, x)
    }
}

data class ComplexVODefViewType(
    val name: String,
    val languageTypes: LanguageTypes,
    val more: MoreLanguageTypes
) : DefViewType {
    override fun name(): String {
        return more.defClassType(name);
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(arg: String): String {
        return more.complexVoDefConstructor(name, arg)
    }
}

data class ListDefViewType(
    val wrappedType: DefViewType,
    val languageTypes: LanguageTypes
) : DefViewType {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun constructor(x: String): String {
        if (wrappedType is BaseDefViewType) {
            return x
        }
        return languageTypes.mapListElements(x, "it", wrappedType.constructor("it"))
    }
}

class DefTypeFactory(
    private val languageTypes: LanguageTypes,
    private val more: MoreLanguageTypes
) {
    fun create(type: ViewType): DefViewType {
        return when (type) {
            is BaseViewType -> BaseDefViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVODefViewType(type, create(type.boxedType) as BaseDefViewType, languageTypes)
            is ComplexVOViewType -> ComplexVODefViewType(type.name, languageTypes, more)
            is ListViewType -> ListDefViewType(create(type.wrappedType), languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
