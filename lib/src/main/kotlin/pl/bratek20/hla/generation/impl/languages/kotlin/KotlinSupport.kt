package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.generation.impl.core.language.*

class KotlinSupport(private val c: DomainContext)
    : LanguageSupport {

    override fun name(): ModuleLanguage {
        return ModuleLanguage.KOTLIN
    }

    override fun types(): LanguageTypes {
        return KotlinTypes()
    }

    override fun structure(): LanguageStructure {
        return KotlinStructure(c)
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        return KotlinAssertsPattern()
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        return KotlinBuildersPattern()
    }

    override fun dtoPattern(): LanguageDtoPattern {
        return KotlinDtoPattern()
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(PackageNameAndImportsExtension(c))
    }
}