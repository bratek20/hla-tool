package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.definitions.BaseType

interface LanguageTypes {
    fun mapBaseType(type: BaseType): String
    fun defaultValueForBaseType(type: BaseType): String

    fun wrapWithList(typeName: String): String
    fun defaultValueForList(): String
    fun mapListElements(listName: String, elementName: String, mapping: String): String

    fun classConstructor(name: String, params: String): String

    fun assertEquals(given: String, expected: String): String
    fun assertListLength(given: String, expected: String): String

    fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String
}

