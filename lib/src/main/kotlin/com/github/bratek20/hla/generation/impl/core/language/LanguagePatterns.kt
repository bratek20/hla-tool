package com.github.bratek20.hla.generation.impl.core.language

interface LanguageAssertsPattern {
    fun assertFunName(name: String): String

    fun expectedClassType(name: String): String
    fun expectedReferencedClassType(name: String): String = expectedClassType(name)
    fun diffFunName(apiStructureName: String): String
    fun indentionForAssertListElements(): Int
}

interface LanguageBuildersPattern {
    fun defClassType(name: String): String

    fun complexVoDefConstructor(name: String, arg: String): String

    fun defOptionalComplexType(name: String): String
    fun mapOptionalDefElement(optionalName: String, elementName: String, mapping: String): String
    fun mapOptionalDefBaseElement(variableName: String): String
}

interface LanguageDtoPattern {
    fun dtoClassType(name: String): String
}