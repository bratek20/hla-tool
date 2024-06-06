package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.definitions.api.BaseType

interface LanguageTypes {
    fun mapBaseType(type: BaseType): String
    fun defaultValueForBaseType(type: BaseType): String

    fun wrapWithList(typeName: String): String
    fun defaultValueForList(): String
    fun mapListElements(listName: String, elementName: String, mapping: String): String
    fun addListElement(listName: String, elementName: String): String
    fun listSize(listName: String): String

    fun wrapWithString(value: String): String

    fun defaultValueForDefOptional(): String

    fun wrapWithOptional(typeName: String): String
    fun mapOptionalElement(optionalName: String, elementName: String, mapping: String): String
    fun unwrapOptional(variableName: String): String
    fun serializeOptional(variableName: String): String
    fun deserializeOptional(variableName: String): String
    fun emptyOptional(): String

    fun checkOptionalEmpty(variableName: String): String

    fun classConstructorCall(className: String): String

    fun assertEquals(given: String, expected: String): String

    fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String

    fun deserializeEnum(enumName: String, variable: String): String
    fun serializeEnum(variableName: String): String

    fun propertyClassConstructorCall(className: String): String

    fun customTypeConstructorName(className: String): String
    fun customTypeConstructorCall(className: String): String
    fun customTypeGetterName(className: String, fieldName: String): String
    fun customTypeGetterCall(className: String, fieldName: String): String
}

