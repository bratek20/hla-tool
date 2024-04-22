package pl.bratek20.hla.generation.impl.core.language

interface LanguageAssertsFixture {
    fun assertFunName(name: String): String
    fun expectedClassType(name: String): String
    fun complexVoAssertion(name: String, given: String, expected: String): String
    fun indentionForAssertListElements(): Int
}

interface LanguageBuildersFixture {
    fun defClassType(name: String): String
    fun complexVoDefConstructor(name: String, arg: String): String
}