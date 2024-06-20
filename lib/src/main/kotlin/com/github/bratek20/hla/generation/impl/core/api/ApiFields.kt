package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.utils.camelToPascalCase


open class ApiTypeField(
    protected val def: FieldDefinition,
    private val factory: ApiTypeFactory
) {
    val name = def.getName()

    val type: ApiType by lazy {
        factory.create(def.getType())
    }

    open fun access(variableName: String): String {
        return "$variableName.$name"
    }

    fun exampleValue(): String? {
        return def.getAttributes().firstOrNull { it.getName() == "example" }?.getValue()
    }

    fun defaultValue(): String? {
        def.getDefaultValue()?.let {
            return mapDefaultValue(it)
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
        val isPublic = def.getAttributes().any { it.getName() == "public" }
        val prefix = type.languageTypes.publicComplexStructureFieldPrefix()
        val finalPrefix = if (prefix.isNotEmpty()) "$prefix " else ""
        return if(isPublic) finalPrefix else "private "
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