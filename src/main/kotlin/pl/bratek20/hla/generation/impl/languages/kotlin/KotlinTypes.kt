package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.Types
import pl.bratek20.hla.model.BuiltInType

class KotlinTypes: Types() {
    override fun mapBuiltInType(type: BuiltInType?): String {
        if (type == null) {
            return "Unit"
        }
        return when (type) {
            BuiltInType.STRING -> "String"
            BuiltInType.INT -> "Int"
            BuiltInType.BOOL -> "Boolean"
        }
    }

    override fun defaultValueForBuiltInType(type: BuiltInType): String {
        return when (type) {
            BuiltInType.STRING -> "\"someValue\""
            BuiltInType.INT -> "0"
            BuiltInType.BOOL -> "false"
        }
    }

    override fun wrapWithList(typeName: String): String {
        return "List<$typeName>"
    }

    override fun defaultValueForList(): String {
        return "emptyList()"
    }
}