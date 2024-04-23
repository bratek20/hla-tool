package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import pl.bratek20.hla.utils.pascalToCamelCase

class TypeScriptAssertsPattern(private val modules: HlaModules) : LanguageAssertsPattern {
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

class TypeScriptBuildersPattern(private val modules: HlaModules) : LanguageBuildersPattern {
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

class TypeScriptDtoPattern(private val modules: HlaModules) : LanguageDtoPattern {
    override fun dtoClassType(name: String): String {
        val base = "${name}Dto"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Web.$base"
        }
    }
}