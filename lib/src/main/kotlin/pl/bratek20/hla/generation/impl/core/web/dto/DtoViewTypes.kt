package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class DtoType<T: ApiType>(
    val view: T
) {
    lateinit var languageTypes: LanguageTypes
    lateinit var pattern: LanguageDtoPattern

    abstract fun name(): String

    abstract fun constructor(arg: String): String

    open fun assignment(fieldName: String): String {
        return fieldName
    }
}

class BaseDtoType(view: BaseApiType): DtoType<BaseApiType>(view) {
    override fun name(): String {
        return view.name()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

abstract class SimpleStructureDtoType(view: SimpleStructureApiType): DtoType<SimpleStructureApiType>(view) {
    override fun name(): String {
        return view.boxedType.name()
    }
}

class SimpleVODtoType(view: SimpleVOApiType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(view.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return "$fieldName.value"
    }
}

class SimpleCustomDtoType(view: SimpleCustomApiType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(view.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterName(view.name, "value") + "($fieldName)"
    }
}

abstract class ComplexStructureDtoType(view: ComplexStructureApiType): DtoType<ComplexStructureApiType>(view) {
    override fun name(): String {
        return pattern.dtoClassType(view.name)
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }
}

class ComplexVODtoType(view: ComplexVOApiType): ComplexStructureDtoType(view) {

    override fun assignment(fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
    }
}

class ComplexCustomDtoType(view: ComplexCustomApiType): ComplexStructureDtoType(view) {
    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterName(view.name, fieldName)
    }
}

class PropertyDtoType(
    view: PropertyApiType
): ComplexStructureDtoType(view)

class ListDtoType(
    private val wrappedType: DtoType<*>,
    view: ListApiType
): DtoType<ListApiType>(view) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDtoType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.constructor("it"))
    }

    override fun assignment(fieldName: String): String {
        if (wrappedType is BaseDtoType) {
            return fieldName
        }
        return languageTypes.mapListElements(fieldName, "it", wrappedType.assignment("it"))
    }
}

class EnumDtoType(view: EnumApiType): DtoType<EnumApiType>(view) {
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
    fun create(type: ApiType): DtoType<*> {
        val result = when (type) {
            is BaseApiType -> BaseDtoType(type)
            is SimpleVOApiType -> SimpleVODtoType(type)
            is ComplexVOApiType -> ComplexVODtoType(type)
            is ListApiType -> ListDtoType(create(type.wrappedType), type)
            is EnumApiType -> EnumDtoType(type)
            is PropertyApiType -> PropertyDtoType(type)
            is SimpleCustomApiType -> SimpleCustomDtoType(type)
            is ComplexCustomApiType -> ComplexCustomDtoType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.pattern = languageDtoPattern

        return result
    }
}