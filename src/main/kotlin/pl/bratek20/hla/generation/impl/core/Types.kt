package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.model.BuiltInType
abstract class Types {

    private fun isBuiltInType(type: String): Boolean {
        return BuiltInType.entries.find { it.name == type.uppercase() } != null
    }

    private fun toBuiltInType(type: String): BuiltInType {
        return BuiltInType.valueOf(type.uppercase())
    }

    protected abstract fun mapBuiltInType(type: BuiltInType): String
    protected abstract fun defaultValueForBuiltInType(type: BuiltInType): String

    fun map(type: String): String {
        if (isBuiltInType(type)) {
            return mapBuiltInType(toBuiltInType(type))
        }
        return type
    }

    fun defaultValue(type: String): String {
        if (isBuiltInType(type)) {
            return defaultValueForBuiltInType(toBuiltInType(type))
        }
        return "null"
    }
}