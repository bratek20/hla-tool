package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class DtoType<T: ApiType>(
    val api: T
) {
    lateinit var languageTypes: LanguageTypes
    lateinit var pattern: LanguageDtoPattern

    abstract fun name(): String

    abstract fun constructor(arg: String): String

    open fun assignment(fieldName: String): String {
        return fieldName
    }
}

class BaseDtoType(api: BaseApiType): DtoType<BaseApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

data class DtoField(
    val dtoType: DtoType<*>,
    val api: ApiField
) {
    val name = api.name

    fun fromApi(fieldName: String): String {
        return dtoType.assignment("$fieldName.$name")
    }

    fun toApi(fieldName: String): String {
        return dtoType.constructor(fieldName)
    }
}

abstract class SimpleStructureDtoType(view: SimpleStructureApiType): DtoType<SimpleStructureApiType>(view) {
    override fun name(): String {
        return api.boxedType.name()
    }
}

class SimpleVODtoType(view: SimpleVOApiType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(api.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return "$fieldName.value"
    }
}

class SimpleCustomDtoType(view: SimpleCustomApiType): SimpleStructureDtoType(view) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(api.name) + "($arg)"
    }

    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterName(api.name, "value") + "($fieldName)"
    }
}

abstract class ComplexStructureDtoType(val fields: List<DtoField>, api: ComplexStructureApiType): DtoType<ComplexStructureApiType>(api) {
    override fun name(): String {
        return pattern.dtoClassType(api.name)
    }

    override fun constructor(arg: String): String {
        return "$arg.toApi()"
    }
}

class ComplexVODtoType(fields: List<DtoField>, api: ComplexVOApiType): ComplexStructureDtoType(fields, api) {

    override fun assignment(fieldName: String): String {
        return "${this.name()}.fromApi($fieldName)"
    }
}

class ComplexCustomDtoType(fields: List<DtoField>, api: ComplexCustomApiType): ComplexStructureDtoType(fields, api) {
    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterName(api.name, fieldName)
    }
}

class PropertyDtoType(fields: List<DtoField>, api: PropertyApiType): ComplexStructureDtoType(fields, api)

class ListDtoType(
    private val wrappedType: DtoType<*>,
    api: ListApiType
): DtoType<ListApiType>(api) {
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

class EnumDtoType(api: EnumApiType): DtoType<EnumApiType>(api) {
    override fun name(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    override fun constructor(arg: String): String {
        return languageTypes.enumConstructor(api.name(), arg)
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
            is SimpleCustomApiType -> SimpleCustomDtoType(type)
            is ListApiType -> ListDtoType(create(type.wrappedType), type)
            is EnumApiType -> EnumDtoType(type)
            is PropertyApiType -> PropertyDtoType(createFields(type.fields), type)
            is ComplexVOApiType -> ComplexVODtoType(createFields(type.fields), type)
            is ComplexCustomApiType -> ComplexCustomDtoType(createFields(type.fields), type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.pattern = languageDtoPattern

        return result
    }

    private fun createFields(fields: List<ApiField>): List<DtoField> {
        return fields.map {
            DtoField(
                dtoType = create(it.type),
                api = it
            )
        }
    }
}