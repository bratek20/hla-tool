package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.model.BaseType

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

    fun indentionForAssertListElements(): Int
}

abstract class MoreLanguageTypes(
    protected val modules: HlaModules
) {
    abstract fun defClassType(name: String): String

    abstract fun expectedClassType(name: String): String

    abstract fun complexVoAssertion(name: String, given: String, expected: String): String

    abstract fun complexVoDefConstructor(name: String, arg: String): String
}