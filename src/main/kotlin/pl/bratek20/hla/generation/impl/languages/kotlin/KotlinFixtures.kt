package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsFixture
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersFixture
import pl.bratek20.hla.utils.pascalToCamelCase

class KotlinAssertsFixture: LanguageAssertsFixture {
    override fun assertFunName(name: String): String {
        return "assert${name}"
    }

    override fun expectedClassType(name: String): String {
        return "(Expected${name}.() -> Unit)"
    }

    override fun complexVoAssertion(name: String, given: String, expected: String): String {
        return "assert$name($given, $expected)"
    }

    override fun indentionForAssertListElements(): Int {
        return 8
    }
}

class KotlinBuildersFixture: LanguageBuildersFixture {
    override fun defClassType(name: String): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        return "${pascalToCamelCase(name)}($arg)"
    }
}