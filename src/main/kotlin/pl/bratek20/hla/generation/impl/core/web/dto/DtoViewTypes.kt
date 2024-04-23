package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.generation.impl.core.api.*
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
    val languageTypes: LanguageTypes
) : DtoViewType {
    override fun name(): String {
        return name + "Dto"
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }

    override fun assignment(name: String): String {
        return "${this.name}Dto.fromApi($name)"
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

class DtoViewTypeFactory(
    private val languageTypes: LanguageTypes
) {
    fun create(type: ViewType): DtoViewType {
        return when (type) {
            is BaseViewType -> BaseDtoViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVODtoViewType(type, create(type.boxedType) as BaseDtoViewType, languageTypes)
            is ComplexVOViewType -> ComplexVODtoViewType(type.name, languageTypes)
            is ListViewType -> ListDtoViewType(create(type.wrappedType), languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}