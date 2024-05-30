package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.utils.pascalToCamelCase

class KotlinAssertsPattern: LanguageAssertsPattern {
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

class KotlinBuildersPattern(
    private val languageTypes: LanguageTypes
): LanguageBuildersPattern {
    override fun defClassType(name: String): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun defOptionalType(name: String): String {
        return "(${name}Def.() -> Unit)?"
    }

    override fun defOptionalBaseType(name: String): String {
        return "$name?"
    }

    override fun mapOptionalDefElement(optionalName: String, elementName: String, mapping: String): String {
        return languageTypes.mapOptionalElement(optionalName, elementName, mapping)
    }

    override fun mapOptionalDefBaseElement(variableName: String): String {
        return variableName
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        return "${pascalToCamelCase(name)}($arg)"
    }
}

class KotlinDtoPattern: LanguageDtoPattern {
    override fun dtoClassType(name: String): String {
        return "${name}Dto"
    }
}