package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

class KotlinTypes: LanguageTypes {
    override fun supportPublicComplexStructureFields(): Boolean {
        return false
    }

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "String"
            BaseType.INT -> "Int"
            BaseType.BOOL -> "Boolean"
            BaseType.VOID -> "Unit"
            BaseType.ANY -> "Any"
        }
    }

    override fun defaultValueForBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "\"someValue\""
            BaseType.INT -> "0"
            BaseType.BOOL -> "false"
            BaseType.VOID -> throw IllegalArgumentException("Void type has no default value")
            BaseType.ANY -> "Any()"
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

    override fun addListElement(listName: String, elementName: String): String {
        return "$listName.add($elementName)"
    }

    override fun listSize(listName: String): String {
        return "$listName.size"
    }

    override fun wrapWithString(value: String): String {
        return "\"$value\""
    }

    override fun defaultValueForDefOptional(): String {
        return "null"
    }

    override fun wrapWithOptional(typeName: String): String {
        return "$typeName?"
    }

    override fun mapOptionalElement(optionalName: String, elementName: String, mapping: String): String {
        return "$optionalName?.let { $elementName -> $mapping }"
    }

    override fun unwrapOptional(variableName: String): String {
        return "$variableName!!"
    }

    override fun serializeOptional(variableName: String): String {
        return variableName
    }

    override fun deserializeOptional(variableName: String): String {
        return variableName
    }

    override fun serializeOptionalForSimpleStructure(variableName: String, className: String): String {
        return "$variableName?.let { it -> it.value }"
    }

    override fun deserializeOptionalForSimpleStructure(variableName: String, className: String): String {
        return "$variableName?.let { it -> $className(it) }"
    }

    override fun emptyOptional(): String {
        return "null"
    }

    override fun checkOptionalEmpty(variableName: String): String {
        return "$variableName == null"
    }

    override fun classConstructorCall(className: String): String {
        return className
    }

    override fun assertEquals(given: String, expected: String): String {
        return "assertThat($given).isEqualTo($expected)"
    }

    override fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String {
        return "$listName.forEachIndexed { $idx, $entry -> $body }"
    }

    override fun deserializeEnum(enumName: String, variable: String): String {
        return "$enumName.valueOf($variable)"
    }

    override fun serializeEnum(variableName: String): String {
        return "$variableName.name"
    }

    override fun propertyClassConstructorCall(className: String): String {
        return "$className.create"
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