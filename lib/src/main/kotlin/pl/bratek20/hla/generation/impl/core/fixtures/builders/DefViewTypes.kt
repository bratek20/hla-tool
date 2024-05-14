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
    val domain: BaseViewType,
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
    val domain: SimpleStructureViewType,
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
    domain: SimpleVOViewType,
    boxedType: BaseDefViewType
) : SimpleStructureDefViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return types.classConstructor(domain.name) + "($arg)"
    }
}

class SimpleCustomDefViewType(
    domain: SimpleCustomViewType,
    boxedType: BaseDefViewType
) : SimpleStructureDefViewType(domain, boxedType) {
    override fun constructor(arg: String): String {
        return types.customTypeClassConstructor(domain.name) + "($arg)"
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
    val view: EnumViewType
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
    fun create(type: ViewType): DefViewType {
        val result = when (type) {
            is BaseViewType -> BaseDefViewType(type)
            is SimpleVOViewType -> SimpleVODefViewType(type, create(type.boxedType) as BaseDefViewType)
            is ComplexVOViewType -> ComplexVODefViewType(type.name)
            is ListViewType -> ListDefViewType(create(type.wrappedType))
            is EnumViewType -> EnumDefViewType(type)
            is SimpleCustomViewType -> SimpleCustomDefViewType(type, create(type.boxedType) as BaseDefViewType)
            is ComplexCustomViewType -> ComplexCustomDefViewType(type.name)
            is PropertyVOViewType -> PropertyVODefViewType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.init(languageTypes, pattern)
        return result
    }
}
