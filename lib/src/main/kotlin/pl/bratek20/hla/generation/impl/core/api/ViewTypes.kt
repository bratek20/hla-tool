package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.definitions.impl.HlaModules

interface ViewType {
    fun name(): String

    fun constructor(arg: String): String {
        return arg
    }

    fun unboxedName(): String {
        return name()
    }

    fun unboxedAssignment(name: String): String {
        return name
    }

    fun unboxedType(): ViewType {
        return this
    }
}

data class BaseViewType(
    val name: BaseType,
    val languageTypes: LanguageTypes
) : ViewType {
    override fun name(): String {
        return languageTypes.mapBaseType(name)
    }
}

data class SimpleVOViewType(
    val name: String,
    val boxedType: BaseViewType,
    val languageTypes: LanguageTypes
) : ViewType {
    override fun name(): String {
        return name;
    }

    override fun unboxedAssignment(name: String): String {
        return "$name.value"
    }

    override fun unboxedName(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(name) + "($arg)"
    }

    override fun unboxedType(): ViewType {
        return boxedType
    }
}


data class ComplexVOViewType(
    val name: String
) : ViewType {
    override fun name(): String {
        return name
    }
}

data class ListViewType(
    val wrappedType: ViewType,
    val languageTypes: LanguageTypes
) : ViewType {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }
}

data class EnumViewType(
    private val domain: EnumDomainType
) : ViewType {
    override fun name(): String {
        return domain.name
    }

    fun defaultValue(): String {
        return name() + "." + domain.defaultValue()
    }
}

class ViewTypeFactory(
    private val modules: HlaModules,
    private val languageTypes: LanguageTypes
) {

    fun create(rawType: TypeDefinition?): ViewType {
        val type = DomainTypeFactory(modules).create(rawType)
        return createFromDomainType(type)
    }

    private fun createFromDomainType(type: DomainType): ViewType {
        return when (type) {
            is ListDomainType -> ListViewType(createFromDomainType(type.wrappedType), languageTypes)
            is SimpleVODomainType -> SimpleVOViewType(type.name, BaseViewType(type.boxedType.name, languageTypes), languageTypes)
            is ComplexVODomainType -> ComplexVOViewType(type.name)
            is BaseDomainType -> BaseViewType(type.name, languageTypes)
            is EnumDomainType -> EnumViewType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}