package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.BaseType
import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

interface DtoViewType {
    fun name(): String

    fun constructor(arg: String): String

    fun assignment(fieldName: String): String {
        return fieldName
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

    override fun assignment(fieldName: String): String {
        return "$fieldName.value"
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

    override fun assignment(fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
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

    override fun assignment(fieldName: String): String {
        if (wrappedType is BaseDtoViewType) {
            return fieldName
        }
        return languageTypes.mapListElements(fieldName, "it", wrappedType.assignment("it"))
    }
}

data class EnumDtoViewType(
    val view: EnumViewType,
    val languageTypes: LanguageTypes
) : DtoViewType {
    override fun name(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    override fun constructor(arg: String): String {
        return languageTypes.enumConstructor(view.name(), arg)
    }

    override fun assignment(fieldName: String): String {
        return languageTypes.enumGetName(fieldName)
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
            is EnumViewType -> EnumDtoViewType(type, languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}