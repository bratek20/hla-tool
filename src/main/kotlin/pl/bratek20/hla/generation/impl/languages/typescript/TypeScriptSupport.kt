package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
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

    override fun assertsFixture(): LanguageAssertsFixture {
        return TypeScriptAssertsFixture(c.modules)
    }

    override fun buildersFixture(): LanguageBuildersFixture {
        return TypeScriptBuildersFixture(c.modules)
    }
}