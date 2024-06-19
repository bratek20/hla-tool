package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.language.*
import com.github.bratek20.hla.utils.camelToPascalCase

class TypeScriptSupport(private val c: DomainContext)
    : LanguageSupport
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.TYPE_SCRIPT
    }

    override fun types(): LanguageTypes {
        return TypeScriptTypes(c.modules)
    }

    override fun filesExtension(): String {
        return "ts"
    }

    override fun adjustDirectoryName(directoryName: String): String {
        return directoryName
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        return TypeScriptAssertsPattern(c.modules)
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        return TypeScriptBuildersPattern(c.modules)
    }

    override fun dtoPattern(): LanguageDtoPattern {
        return TypeScriptDtoPattern(c.modules)
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(ObjectCreationExtension())
    }
}