package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.model.BaseType

interface LanguageTypes {
    fun mapBaseType(type: BaseType): String
    fun defaultValueForBaseType(type: BaseType): String

    fun wrapWithList(typeName: String): String
    fun defaultValueForList(): String
    fun mapList(variableName: String, mapping: String): String

    fun classConstructor(name: String, params: String): String

    fun defClassType(name: String): String

    fun assertEquals(given: String, expected: String): String
    fun assertArraysLength(given: String, expected: String): String

    fun arrayIndexedIteration(array: String, idx: String, entry: String, body: String): String
}