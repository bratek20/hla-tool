package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.FieldDefinition
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.utils.camelToPascalCase

open class ApiTypeField(
    val def: FieldDefinition,
    val type: ApiType
) {
    val name = def.name

    open fun access(variableName: String): String {
        return "$variableName.$name"
    }

    fun exampleValue(): String? {
        return def.attributes.firstOrNull { it.name == "example" }?.value
    }

    // used by velocity
    fun declaration(): String {
        val base = "$name: ${type.name()}"
        if (def.defaultValue != null) {
            return "$base = ${def.defaultValue}"
        }
        return base
    }
}

class ComplexCustomTypeApiField(
    private val className: String,
    def: FieldDefinition,
    type: ApiType,
    private val languageTypes: LanguageTypes
) : ApiTypeField(def, type) {
    override fun access(variableName: String): String {
        return languageTypes.customTypeGetterCall(className, name) + "($variableName)"
    }
}

class SerializableTypeApiField(
    def: FieldDefinition,
    type: ApiType,
): ApiTypeField(def, type) {
    override fun access(variableName: String): String {
        if (type is SimpleStructureApiType) {
            return "$variableName.${getterName()}()"
        }
        return "$variableName.$name"
    }

    fun accessor(): String {
        return if(type is SimpleStructureApiType) "private " else ""
    }

    fun getter(): SerializableTypeGetterOrSetter? {
        if(type is SimpleStructureApiType) {
            return SerializableTypeGetterOrSetter(getterName(), type, name)
        }
        return null
    }

    fun setter(): SerializableTypeGetterOrSetter? {
        if(type is SimpleStructureApiType) {
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