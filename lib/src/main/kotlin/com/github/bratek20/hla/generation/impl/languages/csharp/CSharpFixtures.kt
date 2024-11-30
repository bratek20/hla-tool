package com.github.bratek20.hla.generation.impl.languages.csharp

import com.github.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import com.github.bratek20.hla.generation.impl.languages.typescript.handleReferencing
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.utils.pascalToCamelCase

class CSharpBuildersPattern() : LanguageBuildersPattern {
    override fun defClassType(name: String): String {
        TODO()
    }

    override fun defOptionalComplexType(name: String): String {
        TODO()
    }

    override fun mapOptionalDefElement(optionalName: String, elementName: String, mapping: String): String {
        TODO()
    }

    override fun mapOptionalDefBaseElement(variableName: String): String {
        TODO()
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        return "Build${name}"
    }
}