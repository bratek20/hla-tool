package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

class KotlinTypes: LanguageTypes {
    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "String"
            BaseType.INT -> "Int"
            BaseType.BOOL -> "Boolean"
            BaseType.VOID -> "Unit"
        }
    }

    override fun defaultValueForBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "\"someValue\""
            BaseType.INT -> "0"
            BaseType.BOOL -> "false"
            BaseType.VOID -> throw IllegalArgumentException("Void type has no default value")
        }
    }

    override fun wrapWithList(typeName: String): String {
        return "List<$typeName>"
    }

    override fun defaultValueForList(): String {
        return "emptyList()"
    }

    override fun mapListElements(listName: String, elementName: String, mapping: String): String {
        return "$listName.map { $elementName -> $mapping }"
    }

    override fun classConstructor(className: String): String {
        return className
    }

    override fun assertEquals(given: String, expected: String): String {
        return "assertThat($given).isEqualTo($expected)"
    }

    override fun assertListLength(given: String, expected: String): String {
        return "assertThat($given).hasSize($expected.size)"
    }

    override fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String {
        return "$listName.forEachIndexed { $idx, $entry -> $body }"
    }

    override fun enumConstructor(enumName: String, variable: String): String {
        return "$enumName.valueOf($variable)"
    }

    override fun enumGetName(variableName: String): String {
        return "$variableName.name"
    }

    override fun propertyClassConstructor(className: String): String {
        return className
    }

    override fun customTypeConstructorName(className: String): String {
        return "${pascalToCamelCase(className)}Create"
    }

    override fun customTypeConstructorCall(className: String): String {
        return customTypeConstructorName(className)
    }

    override fun customTypeGetterName(className: String, fieldName: String): String {
        return "${pascalToCamelCase(className)}Get${camelToPascalCase(fieldName)}"
    }

    override fun customTypeGetterCall(className: String, fieldName: String): String {
        return customTypeGetterName(className, fieldName)
    }
}