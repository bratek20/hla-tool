package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.generation.impl.core.language.*

class TypeScriptSupport(private val c: DomainContext)
    : LanguageSupport
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.TYPE_SCRIPT
    }

    override fun types(): LanguageTypes {
        return TypeScriptTypes()
    }

    override fun structure(): LanguageStructure {
        return TypeScriptStructure(c)
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