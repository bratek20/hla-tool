package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.FieldDefinition
import pl.bratek20.hla.utils.camelToPascalCase


open class ApiTypeField(
    private val def: FieldDefinition,
    private val factory: ApiTypeFactory
) {
    val name = def.name

    val type: ApiType by lazy {
        factory.create(def.type)
    }

    open fun access(variableName: String): String {
        return "$variableName.$name"
    }

    fun exampleValue(): String? {
        return def.attributes.firstOrNull { it.name == "example" }?.value
    }

    fun defaultValue(): String? {
        if (def.defaultValue != null) {
            return mapDefaultValue(def.defaultValue)
        }
        return null
    }

    // used by velocity
    fun declaration(): String {
        val base = "$name: ${type.name()}"
        defaultValue()?.let {
            return "$base = $it"
        }
        return base
    }

    private fun mapDefaultValue(value: String): String {
        if (value == "[]") {
            return type.languageTypes.defaultValueForList()
        }
        return value
    }
}

class ComplexCustomTypeApiField(
    private val className: String,
    def: FieldDefinition,
    factory: ApiTypeFactory,
) : ApiTypeField(def, factory) {
    override fun access(variableName: String): String {
        return type.languageTypes.customTypeGetterCall(className, name) + "($variableName)"
    }
}

class SerializableTypeApiField(
    def: FieldDefinition,
    factory: ApiTypeFactory
): ApiTypeField(def, factory) {
    override fun access(variableName: String): String {
        if (typeIsX()) {
            return "$variableName.${getterName()}()"
        }
        return "$variableName.$name"
    }

    private fun typeIsX(): Boolean {
        return type is OptionalApiType || type is SimpleStructureApiType
    }

    fun accessor(): String {
        return if(typeIsX()) "private " else ""
    }

    fun getter(): SerializableTypeGetterOrSetter? {
        if(typeIsX()) {
            return SerializableTypeGetterOrSetter(getterName(), type, name)
        }
        return null
    }

    fun setter(): SerializableTypeGetterOrSetter? {
        if(typeIsX()) {
            return SerializableTypeGetterOrSetter(setterName(), type, name)
        }
        return null
    }

    private fun getterName(): String {
        return "get${camelToPascalCase(name)}"
    }

    private fun setterName(): String {
        return "set${camelToPascalCase(name)}"
    }
}