package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.model.BuiltInType

abstract class Types {

    private fun isBuiltInType(type: String): Boolean {
        return BuiltInType.entries.find { it.name == type.uppercase() } != null
    }

    private fun toBuiltInType(type: String): BuiltInType {
        return BuiltInType.valueOf(type.uppercase())
    }

    protected abstract fun mapBuiltInType(type: BuiltInType?): String
    protected abstract fun defaultValueForBuiltInType(type: BuiltInType): String

    protected abstract fun wrapWithList(typeName: String): String
    protected abstract fun defaultValueForList(): String

    fun map(type: DomainType?): String {
        if (type == null) {
            return mapBuiltInType(null)
        }
        if (type.isList) {
            return wrapWithList(map(type.copy(isList = false)))
        }
        if (isBuiltInType(type.name)) {
            return mapBuiltInType(toBuiltInType(type.name))
        }
        return type.name
    }

    fun defaultValue(type: DomainType): String {
        if (type.isList) {
            return defaultValueForList()
        }
        if (isBuiltInType(type.name)) {
            return defaultValueForBuiltInType(toBuiltInType(type.name))
        }
        return "null"
    }
}