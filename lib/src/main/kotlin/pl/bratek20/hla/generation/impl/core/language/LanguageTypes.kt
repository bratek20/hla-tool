package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.definitions.api.BaseType

interface LanguageTypes {
    fun mapBaseType(type: BaseType): String
    fun defaultValueForBaseType(type: BaseType): String

    fun wrapWithList(typeName: String): String
    fun defaultValueForList(): String
    fun mapListElements(listName: String, elementName: String, mapping: String): String

    fun classConstructor(className: String): String

    fun assertEquals(given: String, expected: String): String
    fun assertListLength(given: String, expected: String): String

    fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String

    fun enumConstructor(enumName: String, variable: String): String
    fun enumGetName(variableName: String): String

    fun propertyClassConstructor(className: String): String
    fun customTypeClassConstructor(className: String): String
    fun customTypeGetterName(className: String, fieldName: String): String
}

