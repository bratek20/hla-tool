package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class DtoViewType {
    lateinit var languageTypes: LanguageTypes
    lateinit var pattern: LanguageDtoPattern

    abstract fun name(): String

    abstract fun constructor(arg: String): String

    open fun assignment(fieldName: String): String {
        return fieldName
    }

    open fun getter(variableName: String, fieldName: String): String {
        return "$variableName.$fieldName"
    }
}

class BaseDtoViewType(
    val domain: BaseViewType,
) : DtoViewType() {
    override fun name(): String {
        return domain.name()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

abstract class SimpleStructureDtoViewType(
    val domain: SimpleStructureViewType,
    val boxedType: BaseDtoViewType,
) : DtoViewType() {
    override fun name(): String {
        return boxedType.name()
    }
}

class SimpleVODtoViewType(
    domain: SimpleVOViewType,
    boxedType: BaseDtoViewType,
) : SimpleStructureDtoViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(domain.name) + "($arg)"
    }

    override fun getter(variableName: String, fieldName: String): String {
        return "$variableName.$fieldName.value"
    }

    override fun assignment(fieldName: String): String {
        return "$fieldName.value"
    }
}

class SimpleCustomDtoViewType(
    domain: SimpleCustomViewType,
    boxedType: BaseDtoViewType,
) : SimpleStructureDtoViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(domain.name) + "($arg)"
    }

    override fun getter(variableName: String, fieldName: String): String {
        return languageTypes.customTypeGetterName(domain.name, fieldName) + "($variableName)"
    }
}

abstract class ComplexStructureDtoViewType(
    val name: String,
) : DtoViewType() {
    override fun name(): String {
        return pattern.dtoClassType(name)
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }
}

class ComplexVODtoViewType(
    name: String,
) : ComplexStructureDtoViewType(name) {
    override fun getter(variableName: String, fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
    }

    override fun assignment(fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
    }
}

class ComplexCustomDtoViewType(
    name: String,
) : ComplexStructureDtoViewType(name) {
    override fun getter(variableName: String, fieldName: String): String {
        return languageTypes.customTypeGetterName(name, fieldName) + "($variableName)"
    }
}

data class ListDtoViewType(
    val wrappedType: DtoViewType,
) : DtoViewType() {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDtoViewType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.constructor("it"))
    }

    override fun getter(variableName: String, fieldName: String): String {
        if (wrappedType is BaseDtoViewType) {
            return fieldName
        }
        return languageTypes.mapListElements(fieldName, "it", wrappedType.getter(variableName, "it"))
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
) : DtoViewType() {
    override fun name(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    override fun constructor(arg: String): String {
        return languageTypes.enumConstructor(view.name(), arg)
    }

    override fun getter(variableName: String, fieldName: String): String {
        return languageTypes.enumGetName(fieldName)
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
        val result = when (type) {
            is BaseViewType -> BaseDtoViewType(type)
            is SimpleVOViewType -> SimpleVODtoViewType(type, create(type.boxedType) as BaseDtoViewType)
            is ComplexVOViewType -> ComplexVODtoViewType(type.name)
            is ListViewType -> ListDtoViewType(create(type.wrappedType))
            is EnumViewType -> EnumDtoViewType(type)
            is SimpleCustomViewType -> SimpleCustomDtoViewType(type, create(type.boxedType) as BaseDtoViewType)
            is ComplexCustomViewType -> ComplexCustomDtoViewType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.pattern = languageDtoPattern

        return result
    }
}