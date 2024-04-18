package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.Types
import pl.bratek20.hla.model.BuiltInType

class TypeScriptTypes: Types() {
    override fun mapBuiltInType(type: BuiltInType): String {
        return when (type) {
            BuiltInType.STRING -> "string"
            BuiltInType.INT -> "number"
            BuiltInType.BOOL -> "boolean"
            BuiltInType.VOID -> "void"
        }
    }

    override fun defaultValueForBuiltInType(type: BuiltInType): String {
        return when (type) {
            BuiltInType.STRING -> "\"someValue\""
            BuiltInType.INT -> "0"
            BuiltInType.BOOL -> "false"
            BuiltInType.VOID -> throw IllegalArgumentException("Void type has no default value")
        }
    }

    override fun wrapWithList(typeName: String): String {
        return "$typeName[]"
    }

    override fun defaultValueForList(): String {
        return "[]"
    }
}