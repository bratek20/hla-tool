package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsFixture
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersFixture
import pl.bratek20.hla.utils.pascalToCamelCase

class TypeScriptAssertsFixture(private val modules: HlaModules) : LanguageAssertsFixture {
    override fun assertFunName(name: String): String {
        return pascalToCamelCase(name)
    }

    override fun expectedClassType(name: String): String {
        val base = "Expected${name}"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Assert.$base"
        }
    }

    override fun complexVoAssertion(name: String, given: String, expected: String): String {
        val base = "${pascalToCamelCase(name)}($given, $expected)"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Assert.$base"
        }
    }

    override fun indentionForAssertListElements(): Int {
        return 12
    }
}

class TypeScriptBuildersFixture(private val modules: HlaModules) : LanguageBuildersFixture {
    override fun defClassType(name: String): String {
        val base = "${name}Def"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Builder.$base"
        }
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        val base = "${pascalToCamelCase(name)}($arg)"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Builder.$base"
        }
    }
}