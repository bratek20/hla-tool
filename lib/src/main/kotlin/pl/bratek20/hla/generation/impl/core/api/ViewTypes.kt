package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.definitions.impl.HlaModules

abstract class ViewType {
    lateinit var languageTypes: LanguageTypes

    abstract fun name(): String

    open fun constructor(arg: String): String {
        return arg
    }

    open fun unboxedName(): String {
        return name()
    }

    open fun unboxedAssignment(name: String): String {
        return name
    }

    open fun unboxedType(): ViewType {
        return this
    }

    open fun constructorName(): String {
        return name()
    }
}

class BaseViewType(
    val name: BaseType
) : ViewType() {
    override fun name(): String {
        return languageTypes.mapBaseType(name)
    }
}

open class SimpleStructureViewType(
    val name: String,
    val boxedType: BaseViewType
) : ViewType() {
    override fun name(): String {
        return name
    }

    override fun unboxedType(): ViewType {
        return boxedType
    }
}

class SimpleVOViewType(
    name: String,
    boxedType: BaseViewType
) : SimpleStructureViewType(name, boxedType) {

    override fun unboxedAssignment(name: String): String {
        return "$name.value"
    }

    override fun unboxedName(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.classConstructor(name)
    }
}

class SimpleCustomViewType(
    name: String,
    boxedType: BaseViewType
) : SimpleStructureViewType(name, boxedType) {

    override fun unboxedAssignment(name: String): String {
        return "$name.value"
    }

    override fun unboxedName(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.customTypeClassConstructor(name)
    }
}




open class ComplexStructureViewType(
    val name: String,
) : ViewType() {
    override fun name(): String {
        return name
    }
}

class ComplexVOViewType(
    name: String
) : ComplexStructureViewType(name)

class ComplexCustomViewType(
    name: String,
) : ComplexStructureViewType(name) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.customTypeClassConstructor(name)
    }
}

class PropertyVOViewType(
    name: String,
) : ComplexStructureViewType(name)

class ListViewType(
    val wrappedType: ViewType,
) : ViewType() {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }
}

class EnumViewType(
    private val domain: EnumDomainType,
) : ViewType() {
    override fun name(): String {
        return domain.name
    }

    fun defaultValue(): String {
        return name() + "." + domain.defaultValue()
    }
}

class ViewTypeFactory(
    private val modules: HlaModules,
    private val languageTypes: LanguageTypes
) {

    fun create(rawType: TypeDefinition?): ViewType {
        val type = DomainTypeFactory(modules).create(rawType)
        return createFromDomainType(type)
    }

    private fun createBaseViewType(type: BaseType): BaseViewType {
        val result = BaseViewType(type)
        result.languageTypes = languageTypes
        return result
    }

    private fun createFromDomainType(type: DomainType): ViewType {
        val viewType = when (type) {
            is ListDomainType -> ListViewType(createFromDomainType(type.wrappedType))
            is SimpleVODomainType -> SimpleVOViewType(type.name, createBaseViewType(type.boxedType.name))
            is ComplexVODomainType -> ComplexVOViewType(type.name)
            is BaseDomainType -> BaseViewType(type.name)
            is EnumDomainType -> EnumViewType(type)
            is SimpleCustomDomainType -> SimpleCustomViewType(type.name, createBaseViewType(type.boxedType.name))
            is ComplexCustomDomainType -> ComplexCustomViewType(type.name)
            is PropertyVODomainType -> PropertyVOViewType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        viewType.languageTypes = languageTypes

        return viewType
    }
}