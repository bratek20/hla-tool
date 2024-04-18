package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.model.BuiltInType

abstract class Types {

    private fun isBuiltInType(type: String): Boolean {
        return BuiltInType.entries.find { it.name == type.uppercase() } != null
    }

    private fun toBuiltInType(type: String): BuiltInType {
        return BuiltInType.valueOf(type.uppercase())
    }

    fun mapBaseType(type: String): String {
        return mapBuiltInType(toBuiltInType(type))
    }

    fun defaultValueForBaseType(type: String): String {
        return defaultValueForBuiltInType(toBuiltInType(type))
    }

    protected abstract fun mapBuiltInType(type: BuiltInType): String
    protected abstract fun defaultValueForBuiltInType(type: BuiltInType): String

    abstract fun wrapWithList(typeName: String): String
    protected abstract fun defaultValueForList(): String
}