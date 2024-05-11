package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

interface DtoViewType {
    fun name(): String

    fun constructor(arg: String): String

    fun assignment(name: String): String {
        return name
    }
}

data class BaseDtoViewType(
    val domain: BaseViewType,
    val languageTypes: LanguageTypes
) : DtoViewType {
    override fun name(): String {
        return domain.name()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

data class SimpleVODtoViewType(
    val domain: SimpleVOViewType,
    val boxedType: BaseDtoViewType,
    val languageTypes: LanguageTypes
) : DtoViewType {
    override fun name(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(domain.name, arg)
    }

    override fun assignment(name: String): String {
        return "$name.value"
    }
}

data class ComplexVODtoViewType(
    val name: String,
    val pattern: LanguageDtoPattern
) : DtoViewType {
    override fun name(): String {
        return pattern.dtoClassType(name)
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }

    override fun assignment(name: String): String {
        return "${this.name()}.fromApi($name)"
    }
}

data class ListDtoViewType(
    val wrappedType: DtoViewType,
    val languageTypes: LanguageTypes
) : DtoViewType {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDtoViewType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.constructor("it"))
    }

    override fun assignment(name: String): String {
        if (wrappedType is BaseDtoViewType) {
            return name
        }
        return languageTypes.mapListElements(name, "it", wrappedType.assignment("it"))
    }
}

data class EnumDtoViewType(
    val view: EnumViewType
) : DtoViewType {
    override fun name(): String {
        return "String"
    }

    override fun constructor(arg: String): String {
        return "${view.name()}.valueOf($arg)"
    }

    override fun assignment(name: String): String {
        return "$name.name"
    }
}

class DtoViewTypeFactory(
    private val languageTypes: LanguageTypes,
    private val languageDtoPattern: LanguageDtoPattern
) {
    fun create(type: ViewType): DtoViewType {
        return when (type) {
            is BaseViewType -> BaseDtoViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVODtoViewType(type, create(type.boxedType) as BaseDtoViewType, languageTypes)
            is ComplexVOViewType -> ComplexVODtoViewType(type.name, languageDtoPattern)
            is ListViewType -> ListDtoViewType(create(type.wrappedType), languageTypes)
            is EnumViewType -> EnumDtoViewType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}