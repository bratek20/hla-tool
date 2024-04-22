package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.model.*

interface ViewType {
    fun name(): String
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
    val boxedType: BaseViewType
) : ViewType {
    override fun name(): String {
        return name;
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

class ViewTypeFactory(
    private val modules: HlaModules,
    private val languageTypes: LanguageTypes
) {

    fun create(rawType: Type?): ViewType {
        val type = DomainTypeFactory(modules).create(rawType)
        return create2(type)
    }

    private fun create2(type: DomainType): ViewType {
        return when (type) {
            is ListDomainType -> ListViewType(create2(type.wrappedType), languageTypes)
            is SimpleVODomainType -> SimpleVOViewType(type.name, BaseViewType(type.boxedType.name, languageTypes))
            is ComplexVODomainType -> ComplexVOViewType(type.name)
            is BaseDomainType -> BaseViewType(type.name, languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}