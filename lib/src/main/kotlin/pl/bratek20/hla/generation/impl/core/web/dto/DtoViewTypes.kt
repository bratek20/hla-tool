package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class DtoType<T: ViewType>(
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

class BaseDtoType(view: BaseViewType): DtoType<BaseViewType>(view) {
    override fun name(): String {
        return view.name()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

abstract class SimpleStructureDtoType(view: SimpleStructureViewType): DtoType<SimpleStructureViewType>(view) {
    override fun name(): String {
        return view.boxedType.name()
    }
}

class SimpleVODtoType(view: SimpleVOViewType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(view.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return "$fieldName.value"
    }
}

class SimpleCustomDtoType(view: SimpleCustomViewType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(view.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return "get${view.name}Value($fieldName)"
    }
}

abstract class ComplexStructureDtoType(view: ComplexStructureViewType): DtoType<ComplexStructureViewType>(view) {
    override fun name(): String {
        return pattern.dtoClassType(view.name)
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }
}

class ComplexVODtoType(view: ComplexVOViewType): ComplexStructureDtoType(view) {

    override fun assignment(fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
    }
}

class ComplexCustomDtoType(view: ComplexCustomViewType): ComplexStructureDtoType(view)

class ListDtoType(
    private val wrappedType: DtoType<*>,
    view: ListViewType
): DtoType<ListViewType>(view) {
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

class EnumDtoType(view: EnumViewType): DtoType<EnumViewType>(view) {
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
    fun create(type: ViewType): DtoType<*> {
        val result = when (type) {
            is BaseViewType -> BaseDtoType(type)
            is SimpleVOViewType -> SimpleVODtoType(type)
            is ComplexVOViewType -> ComplexVODtoType(type)
            is ListViewType -> ListDtoType(create(type.wrappedType), type)
            is EnumViewType -> EnumDtoType(type)
            is SimpleCustomViewType -> SimpleCustomDtoType(type)
            is ComplexCustomViewType -> ComplexCustomDtoType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.pattern = languageDtoPattern

        return result
    }
}