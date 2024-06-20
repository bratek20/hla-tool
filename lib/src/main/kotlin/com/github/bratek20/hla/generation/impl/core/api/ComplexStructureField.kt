package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.utils.camelToPascalCase

open class ComplexStructureField(
    protected val def: FieldDefinition,
    private val factory: ApiTypeFactory
) {
    val name = def.getName()

    val type: ApiType by lazy {
        factory.create(def.getType())
    }

    fun access(variableName: String): String {
        if (type is ComplexCustomApiType) {
            return accessComplexCustomType(variableName)
        }
        return "$variableName.${getterName()}()"
    }

    private fun accessComplexCustomType(variableName: String): String {
        return type.languageTypes.customTypeGetterCall(type.name(), name) + "($variableName)"
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
    fun accessor(): String {
        val isPublic = def.getAttributes().any { it.getName() == "public" }
        val prefix = type.languageTypes.publicComplexStructureFieldPrefix()
        val finalPrefix = if (prefix.isNotEmpty()) "$prefix " else ""
        return if(isPublic) finalPrefix else "private "
    }

    // used by velocity
    fun classDeclaration(): String {
        return "${accessor()}val ${privateName()}: ${type.serializableName()}"
    }

    // used by velocity
    fun createDeclaration(): String {
        val base = "${privateName()}: ${type.name()}"
        defaultValue()?.let {
            return "$base = $it"
        }
        return base
    }

    fun privateName(): String {
        def.getAttributes().firstOrNull { it.getName() == "from" }?.let {
            return it.getValue()
        }
        return name
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

    fun getter(): ComplexStructureGetterOrSetter {
        return ComplexStructureGetterOrSetter(getterName(), type, name)
    }

    fun setter(): ComplexStructureGetterOrSetter {
        return ComplexStructureGetterOrSetter(setterName(), type, name)
    }

    fun getterName(): String {
        return "get${camelToPascalCase(name)}"
    }

    fun setterName(): String {
        return "set${camelToPascalCase(name)}"
    }
}