package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class DefViewType {
    protected lateinit var types: LanguageTypes
    protected lateinit var pattern: LanguageBuildersPattern

    fun init(languageTypes: LanguageTypes, fixture: LanguageBuildersPattern) {
        this.types = languageTypes
        this.pattern = fixture
    }

    abstract fun name(): String

    abstract fun defaultValue(): String

    abstract fun constructor(arg: String): String
}

class BaseDefViewType(
    val domain: BaseApiType,
) : DefViewType() {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return types.defaultValueForBaseType(domain.name)
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

abstract class SimpleStructureDefViewType(
    val domain: SimpleStructureApiType,
    val boxedType: BaseDefViewType
) : DefViewType() {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }
}

class SimpleVODefViewType(
    domain: SimpleVOApiType,
    boxedType: BaseDefViewType
) : SimpleStructureDefViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return types.classConstructor(domain.name) + "($arg)"
    }
}

class SimpleCustomDefViewType(
    domain: SimpleCustomApiType,
    boxedType: BaseDefViewType
) : SimpleStructureDefViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return types.customTypeConstructorCall(domain.name) + "($arg)"
    }

}

open class ComplexStructureDefViewType(
    val name: String
) : DefViewType() {
    override fun name(): String {
        return pattern.defClassType(name);
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun constructor(arg: String): String {
        return pattern.complexVoDefConstructor(name, arg)
    }
}

class ComplexVODefViewType(
    name: String
) : ComplexStructureDefViewType(name)

class ComplexCustomDefViewType(
    name: String
) : ComplexStructureDefViewType(name)

class PropertyVODefViewType(
    name: String
) : ComplexStructureDefViewType(name)

data class ListDefViewType(
    val wrappedType: DefViewType
) : DefViewType() {
    override fun name(): String {
        return types.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return types.defaultValueForList()
    }

    override fun constructor(arg: String): String {
        if (wrappedType is BaseDefViewType) {
            return arg
        }
        return types.mapListElements(arg, "it", wrappedType.constructor("it"))
    }
}

data class EnumDefViewType(
    val view: EnumApiType
) : DefViewType() {
    override fun name(): String {
        return view.name()
    }

    override fun defaultValue(): String {
        return view.defaultValue()
    }

    override fun constructor(arg: String): String {
        return arg
    }
}

class DefTypeFactory(
    private val languageTypes: LanguageTypes,
    private val pattern: LanguageBuildersPattern
) {
    fun create(type: ApiType): DefViewType {
        val result = when (type) {
            is BaseApiType -> BaseDefViewType(type)
            is SimpleVOApiType -> SimpleVODefViewType(type, create(type.boxedType) as BaseDefViewType)
            is ComplexVOApiType -> ComplexVODefViewType(type.name)
            is ListApiType -> ListDefViewType(create(type.wrappedType))
            is EnumApiType -> EnumDefViewType(type)
            is SimpleCustomApiType -> SimpleCustomDefViewType(type, create(type.boxedType) as BaseDefViewType)
            is ComplexCustomApiType -> ComplexCustomDefViewType(type.name)
            is PropertyApiType -> PropertyVODefViewType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.init(languageTypes, pattern)
        return result
    }
}
