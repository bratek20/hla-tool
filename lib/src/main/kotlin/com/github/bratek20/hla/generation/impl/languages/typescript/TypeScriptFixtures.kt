package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.definitions.impl.HlaModules
import com.github.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageDtoPattern
import com.github.bratek20.hla.utils.pascalToCamelCase

class TypeScriptAssertsPattern(private val modules: HlaModules) : LanguageAssertsPattern {
    override fun assertFunName(name: String): String {
        return pascalToCamelCase(name)
    }

    override fun expectedClassType(name: String): String {
        val base = "Expected${name}"
        return handleReferencing(modules, name, base, null)
    }

    override fun diffFunName(apiStructureName: String): String {
        val base = "diff${apiStructureName}"
        return handleReferencing(modules, apiStructureName, base, null)
    }

    override fun indentionForAssertListElements(): Int {
        return 12
    }
}

class TypeScriptBuildersPattern(private val modules: HlaModules) : LanguageBuildersPattern {
    override fun defClassType(name: String): String {
        val base = "${name}Def"
        return handleReferencing(modules, name, base, "Builder")
    }

    override fun defOptionalType(name: String): String {
        return defClassType(name)
    }

    override fun defOptionalBaseType(name: String): String {
        return name
    }

    override fun mapOptionalDefElement(optionalName: String, elementName: String, mapping: String): String {
        return "Optional.of($optionalName).map($elementName => $mapping)"
    }

    override fun mapOptionalDefBaseElement(variableName: String): String {
        return "Optional.of($variableName)"
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        val base = "${pascalToCamelCase(name)}($arg)"
        return handleReferencing(modules, name, base, "Builder")
    }
}

class TypeScriptDtoPattern(private val modules: HlaModules) : LanguageDtoPattern {
    override fun dtoClassType(name: String): String {
        val base = "${name}Dto"
        return handleReferencing(modules, name, base, "Web")
    }
}

