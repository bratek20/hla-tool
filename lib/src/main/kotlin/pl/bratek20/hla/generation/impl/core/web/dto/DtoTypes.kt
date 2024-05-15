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

    open fun name(): String {
        return api.name()
    }

    open fun toApi(arg: String): String {
        return arg
    }

    open fun fromApi(variableName: String): String {
        return variableName
    }
}

class BaseDtoType(api: BaseApiType): DtoType<BaseApiType>(api)



abstract class SimpleStructureDtoType(view: SimpleStructureApiType): DtoType<SimpleStructureApiType>(view) {
    override fun name(): String {
        return api.boxedType.name()
    }
}

class SimpleVODtoType(view: SimpleVOApiType): SimpleStructureDtoType(view) {
    override fun toApi(arg: String): String {
        return languageTypes.classConstructor(api.name) + "($arg)"
    }

    override fun fromApi(variableName: String): String {
        return "$variableName.value"
    }
}

class SimpleCustomDtoType(view: SimpleCustomApiType): SimpleStructureDtoType(view) {
    override fun toApi(arg: String): String {
        return languageTypes.customTypeClassConstructor(api.name) + "($arg)"
    }
}

data class DtoField(
    val type: DtoType<*>,
    val api: ApiTypeField<*>
) {
    val name = api.name

    fun fromApi(variableName: String): String {
        return type.fromApi(api.access(variableName))
    }

    fun toApi(): String {
        return type.toApi(name)
    }

    fun toApi(variable: String): String {
        return type.toApi("$variable.$name")
    }
}

abstract class ComplexStructureDtoType(val fields: List<DtoField>, api: ComplexStructureApiType): DtoType<ComplexStructureApiType>(api) {
    override fun name(): String {
        return pattern.dtoClassType(api.name)
    }

    override fun toApi(arg: String): String {
        return "$arg.toApi()"
    }

    open fun fromApi(field: DtoField, variableName: String): String {
        return field.fromApi(variableName)
    }
}

class ComplexVODtoType(fields: List<DtoField>, api: ComplexVOApiType): ComplexStructureDtoType(fields, api) {

    override fun fromApi(variableName: String): String {
        return "${this.name()}.fromApi($variableName)"
    }
}

class ComplexCustomDtoType(fields: List<DtoField>, api: ComplexCustomApiType): ComplexStructureDtoType(fields, api) {
    override fun fromApi(variableName: String): String {
        return languageTypes.customTypeGetterName(api.name, variableName)
    }

    override fun fromApi(field: DtoField, variableName: String): String {
        return field.api.access(languageTypes.customTypeGetterName(api.name, field.name) + "($variableName)")
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

    override fun toApi(arg: String): String {
        if (wrappedType is BaseDtoType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.toApi("it"))
    }

    override fun fromApi(variableName: String): String {
        if (wrappedType is BaseDtoType) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.fromApi("it"))
    }
}

class EnumDtoType(api: EnumApiType): DtoType<EnumApiType>(api) {
    override fun name(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    override fun toApi(arg: String): String {
        return languageTypes.enumConstructor(api.name(), arg)
    }

    override fun fromApi(variableName: String): String {
        return languageTypes.enumGetName(variableName)
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

    private fun createFields(fields: List<ApiTypeField<*>>): List<DtoField> {
        return fields.map {
            DtoField(
                type = create(it.type),
                api = it
            )
        }
    }
}