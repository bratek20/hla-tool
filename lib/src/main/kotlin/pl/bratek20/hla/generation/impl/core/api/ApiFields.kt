package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.FieldDefinition
import pl.bratek20.hla.utils.camelToPascalCase


open class ApiTypeField(
    protected val def: FieldDefinition,
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
        if (value == "empty") {
            return type.languageTypes.emptyOptional()
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
        return "$variableName.${getterName()}()"
    }

    // used by velocity
    fun accessor(): String {
        val isPublic = def.attributes.firstOrNull { it.name == "public" } != null
        val publicSupported = type.languageTypes.supportPublicComplexStructureFields()
        return if(isPublic && publicSupported) "" else "private "
    }

    fun getter(): SerializableTypeGetterOrSetter {
        return SerializableTypeGetterOrSetter(getterName(), type, name)
    }

    fun setter(): SerializableTypeGetterOrSetter {
        return SerializableTypeGetterOrSetter(setterName(), type, name)
    }

    private fun getterName(): String {
        return "get${camelToPascalCase(name)}"
    }

    private fun setterName(): String {
        return "set${camelToPascalCase(name)}"
    }
}