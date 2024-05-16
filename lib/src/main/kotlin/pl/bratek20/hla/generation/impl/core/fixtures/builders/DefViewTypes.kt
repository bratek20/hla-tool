package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.utils.pascalToCamelCase

abstract class DefType<T: ApiType>(
    val api: T
) {
    protected val languageTypes: LanguageTypes
        get() = api.languageTypes

    lateinit var pattern: LanguageBuildersPattern

    abstract fun name(): String

    abstract fun defaultValue(): String

    abstract fun constructor(arg: String): String
}

class BaseDefType(
    api: BaseApiType,
) : DefType<BaseApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun defaultValue(): String {
        return api.languageTypes.defaultValueForBaseType(api.name)
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

abstract class SimpleStructureDefType<T: SimpleStructureApiType>(
    api: T,
    private val boxedType: BaseDefType
) : DefType<T>(api) {
    override fun constructor(arg: String): String {
        return api.constructor(arg)
    }

    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }
}

class SimpleVODefType(
    api: SimpleVOApiType,
    boxedType: BaseDefType
) : SimpleStructureDefType<SimpleVOApiType>(api, boxedType) {
}

class SimpleCustomDefType(
    api: SimpleCustomApiType,
    boxedType: BaseDefType
) : SimpleStructureDefType<SimpleCustomApiType>(api, boxedType) {
}

class DefField(
    val type: DefType<*>,
    val api: ApiTypeField
) {
    val name = api.name
}

open class ComplexStructureDefType(
    api: ComplexStructureApiType<*>,
    val fields: List<DefField>
) : DefType<ComplexStructureApiType<*>>(api) {
    fun funName(): String {
        return pascalToCamelCase(api.name())
    }

    fun defName(): String {
        return api.name() + "Def"
    }

    override fun name(): String {
        return pattern.defClassType(api.name());
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(arg: String): String {
        return pattern.complexVoDefConstructor(api.name(), arg)
    }
}

class ComplexVODefType(
    api: ComplexStructureApiType<*>,
    fields: List<DefField>
) : ComplexStructureDefType(api, fields)

class ComplexCustomDefType(
    api: ComplexStructureApiType<*>,
    fields: List<DefField>
) : ComplexStructureDefType(api, fields)

class PropertyDefType(
    api: ComplexStructureApiType<*>,
    fields: List<DefField>
) : ComplexStructureDefType(api, fields)

class ListDefType(
    api: ListApiType,
    val wrappedType: DefType<*>
) : DefType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDefType) {
            return arg
        }
        return languageTypes.mapListElements(arg, "it", wrappedType.constructor("it"))
    }
}

class EnumDefType(
    api: EnumApiType
) : DefType<EnumApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun defaultValue(): String {
        return api.defaultValue()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

class DefTypeFactory(
    private val pattern: LanguageBuildersPattern
) {
    fun create(type: ApiType): DefType<*> {
        val result = when (type) {
            is BaseApiType -> BaseDefType(type)
            is SimpleVOApiType -> SimpleVODefType(type, create(type.boxedType) as BaseDefType)
            is ComplexVOApiType -> ComplexVODefType(type, createFields(type.fields))
            is ListApiType -> ListDefType(type, create(type.wrappedType))
            is EnumApiType -> EnumDefType(type)
            is SimpleCustomApiType -> SimpleCustomDefType(type, create(type.boxedType) as BaseDefType)
            is ComplexCustomApiType -> ComplexCustomDefType(type, createFields(type.fields))
            is PropertyApiType -> PropertyDefType(type, createFields(type.fields))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.pattern = pattern

        return result
    }

    private fun createFields(fields: List<ApiTypeField>): List<DefField> {
        return fields.map { DefField(create(it.type), it) }
    }
}
