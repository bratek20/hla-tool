package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

interface DefViewType {
    fun name(): String

    fun defaultValue(): String

    fun constructor(arg: String): String
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

    override fun constructor(arg: String): String {
        return arg
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

    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(domain.name, arg)
    }
}

data class ComplexVODefViewType(
    val name: String,
    val languageTypes: LanguageTypes,
    val fixture: LanguageBuildersPattern
) : DefViewType {
    override fun name(): String {
        return fixture.defClassType(name);
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(arg: String): String {
        return fixture.complexVoDefConstructor(name, arg)
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

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDefViewType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.constructor("it"))
    }
}

class DefTypeFactory(
    private val languageTypes: LanguageTypes,
    private val more: LanguageBuildersPattern
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
