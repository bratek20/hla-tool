package pl.bratek20.hla.generation.impl.core.language

interface LanguageAssertsPattern {
    fun assertFunName(name: String): String
    fun expectedClassType(name: String): String
    fun complexVoAssertion(name: String, given: String, expected: String): String
    fun indentionForAssertListElements(): Int
}

interface LanguageBuildersPattern {
    fun defClassType(name: String): String
    fun complexVoDefConstructor(name: String, arg: String): String
}

interface LanguageDtoPattern {
    fun dtoClassType(name: String): String
}