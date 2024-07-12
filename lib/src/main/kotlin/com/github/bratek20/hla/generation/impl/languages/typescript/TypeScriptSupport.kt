package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.codebuilder.Language
import com.github.bratek20.hla.codebuilder.TypeScript
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.language.*

class TypeScriptSupport(private val c: DomainContext)
    : LanguageSupport
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.TYPE_SCRIPT
    }

    override fun types(): LanguageTypes {
        return TypeScriptTypes(c.queries)
    }

    override fun filesExtension(): String {
        return "ts"
    }

    override fun adjustDirectoryName(directoryName: String): String {
        return directoryName
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        return TypeScriptAssertsPattern(c.queries)
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        return TypeScriptBuildersPattern(c.queries)
    }

    override fun dtoPattern(): LanguageDtoPattern {
        return TypeScriptDtoPattern(c.queries)
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(ObjectCreationExtension())
    }

    override fun base(): Language {
        return TypeScript()
    }
}